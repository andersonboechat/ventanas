package br.com.abware.accountmgm.persistence.manager;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;

import com.liferay.portal.NoSuchGroupException;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.GroupConstants;
import com.liferay.portal.model.Resource;
import com.liferay.portal.model.ResourceConstants;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.ResourceLocalServiceUtil;

import br.com.abware.accountmgm.persistence.entity.FlatEntity;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Person;

public class NewFlatManagerImpl extends JCondoManager<FlatEntity, Flat> {

	@Override
	protected Class<Flat> getModelClass() {
		return Flat.class;
	}

	@Override
	protected Class<FlatEntity> getEntityClass() {
		return FlatEntity.class;
	}

	public Flat save(Flat flat) throws Exception {
		Flat f = super.save(flat);

		Group group;

		try {
			group = GroupLocalServiceUtil.getGroup(helper.getCompanyId(), flat.toString());
		} catch (NoSuchGroupException e) {
			group = GroupLocalServiceUtil.addGroup(helper.getUserId(), Resource.class.getName(), f.getId(), flat.toString(), 
												   StringUtils.EMPTY, GroupConstants.TYPE_SITE_PRIVATE, 
												   f.getBlock() + "-" + f.getNumber(), false, true, null);
		}

		ResourceLocalServiceUtil.addResources(helper.getCompanyId(), group.getGroupId(), helper.getUserId(), 
											  Flat.class.getName(), f.getId(), false, false, false);

		f.setDomainId(group.getGroupId());

		return super.save(f);
	}

	public void delete(Flat flat) throws Exception {
		super.delete(flat);
		ResourceLocalServiceUtil.deleteResource(helper.getCompanyId(), Flat.class.getName(), ResourceConstants.SCOPE_COMPANY, helper.getCompanyId());
		ResourceLocalServiceUtil.deleteResource(helper.getCompanyId(), Flat.class.getName(), ResourceConstants.SCOPE_GROUP, flat.getDomainId());
		ResourceLocalServiceUtil.deleteResource(helper.getCompanyId(), Flat.class.getName(), ResourceConstants.SCOPE_INDIVIDUAL, flat.getId());
		GroupLocalServiceUtil.deleteGroup(flat.getDomainId());
	}

	public List<Flat> findByPerson(Person person) throws Exception {
		List<Flat> flats = new ArrayList<Flat>();
		String queryString = "FROM FlatEntity WHERE domainId = :id";

		try {
			openManager("FlatManager.findByPerson");
			for (Group group : GroupLocalServiceUtil.getUserGroups(person.getUserId())) {
				try {
					Query query = em.createQuery(queryString);
					query.setParameter("id", group.getClassPK());
					flats.add(getModel((FlatEntity) query.getSingleResult()));
				} catch (Exception e) {
					
				}
			}
		} finally {
			closeManager("FlatManager.findByPerson");
		}

		return flats;
	}

}

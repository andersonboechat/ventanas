package br.com.abware.accountmgm.persistence.manager;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;

import com.liferay.portal.model.Group;
import com.liferay.portal.model.GroupConstants;
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

		if (flat.getRelatedId() == 0) {
			Group group = GroupLocalServiceUtil.addGroup(helper.getUserId(), Group.class.getName(), 0, flat.toString(), 
														 StringUtils.EMPTY, GroupConstants.TYPE_SITE_PRIVATE, 
														 "/" + flat.getBlock() + "-" + flat.getNumber(), true, true, null);
			ResourceLocalServiceUtil.addResources(helper.getCompanyId(), group.getGroupId(), helper.getUserId(), 
												  Flat.class.getName(), f.getId(), false, true, false);
			f.setRelatedId(group.getGroupId());
			f = super.save(f);
		}

		return f;
	}

	public void delete(Flat flat) throws Exception {
		super.delete(flat);
		ResourceLocalServiceUtil.deleteResource(helper.getCompanyId(), Flat.class.getName(), ResourceConstants.SCOPE_COMPANY, helper.getCompanyId());
		ResourceLocalServiceUtil.deleteResource(helper.getCompanyId(), Flat.class.getName(), ResourceConstants.SCOPE_GROUP, flat.getRelatedId());
		ResourceLocalServiceUtil.deleteResource(helper.getCompanyId(), Flat.class.getName(), ResourceConstants.SCOPE_INDIVIDUAL, flat.getId());
		GroupLocalServiceUtil.deleteGroup(flat.getRelatedId());
	}

	public List<Flat> findByPerson(Person person) throws Exception {
		List<Flat> flats = new ArrayList<Flat>();
		String queryString = "FROM FlatEntity WHERE relatedId = :id";

		try {
			openManager("FlatManager.findByPerson");
			for (Group group : GroupLocalServiceUtil.getUserGroups(person.getUserId())) {
				try {
					Query query = em.createQuery(queryString);
					query.setParameter("id", group.getGroupId());
					flats.add(getModel((FlatEntity) query.getSingleResult()));
				} catch (Exception e) {
					
				}
			}
		} finally {
			closeManager("FlatManager.findByPerson");
		}

		return flats;
	}

	public Flat findByNumberAndBlock(int number, int block) throws Exception {
		String key = generateKey();

		try {
			openManager(key);
			String queryString = "FROM FlatEntity WHERE number = :number and block = :block";
			Query query = em.createQuery(queryString);
			query.setParameter("number", number);
			query.setParameter("block", block);
			return getModel((FlatEntity) query.getSingleResult());
		} catch (NoResultException e) {
			return null;
		} finally {
			closeManager(key);
		}

	}
}

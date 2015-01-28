package br.com.abware.accountmgm.persistence.manager;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;

import com.liferay.portal.model.Group;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.ServiceContext;

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
		String number = StringUtils.leftPad(String.valueOf(flat.getNumber()), 4, "0");
		Group group = GroupLocalServiceUtil.addGroup(helper.getUserId(), Group.class.getName(), 0L, 
													 flat.getBlock() + "/" + number, StringUtils.EMPTY, 3, 
													 "/" + flat.getBlock() + "-" + number, false, true, 
													 new ServiceContext());
		flat.setId(group.getGroupId());
		super.save(flat);
		return flat;
	}
	
	public void delete(Flat flat) throws Exception {
		super.delete(flat);
		GroupLocalServiceUtil.deleteGroup(flat.getId());
	}
	
	public List<Flat> findByPerson(Person person) throws Exception {
		List<Flat> flats = new ArrayList<Flat>();
		String queryString = "FROM FlatEntity WHERE id = :id";

		try {
			openManager("FlatManager.findByPerson");
			for (Group group : GroupLocalServiceUtil.getUserGroups(person.getId())) {
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

	public void assignTo(Person person, long[] flatIds) throws Exception {
		GroupLocalServiceUtil.addUserGroups(person.getId(), flatIds);
	}

	public void removeFrom(Person person, long[] flatIds) throws Exception {
		GroupLocalServiceUtil.unsetUserGroups(person.getId(), flatIds);
	}

}

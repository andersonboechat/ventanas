package br.com.abware.accountmgm.persistence.manager;

import br.com.abware.jcondo.core.model.Condominium;
import br.com.abware.jcondo.exception.PersistenceException;

import com.liferay.portal.model.Group;
import com.liferay.portal.service.GroupLocalServiceUtil;

public class CondoManagerImpl extends LiferayManager<Group, Condominium> {

	@Override
	protected Class<Condominium> getModelClass() {
		return Condominium.class;
	}

	@Override
	protected Group getEntity(Condominium model) throws PersistenceException {
		try {
			return GroupLocalServiceUtil.getGroup(model.getId());
		} catch (Exception e) {
			throw new PersistenceException(e, "");
		}
	}
	
	protected Condominium getModel(Group entity) throws Exception {
		return new Condominium(entity.getGroupId(), entity.getDescriptiveName());
	}

	public Condominium findCondominium() throws PersistenceException {
		try {
			return getModel(GroupLocalServiceUtil.getGroup(helper.getCompanyId()));
		} catch (Exception e) {
			throw new PersistenceException(e, "");
		}
	}
}

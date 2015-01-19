package br.com.abware.accountmgm.persistence.manager;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;

import br.com.abware.jcondo.core.model.BaseModel;
import br.com.abware.jcondo.exception.PersistenceException;

import com.liferay.portal.model.PersistedModel;

public abstract class AbstractManager<Entity extends PersistedModel, Model extends BaseModel> {

	protected static final long COMPANY = 10153;
	
	protected abstract Class<Model> getModelClass();

	protected abstract Entity getEntity(Model model) throws PersistenceException;
	
	protected Model getModel(Entity entity) throws Exception {
		Model model = getModelClass().newInstance();
		BeanUtils.copyProperties(model, entity);
		return model;
	}

	protected List<Model> getModels(List<Entity> entities) throws PersistenceException {
		try {
			List<Model> models = new ArrayList<Model>();

			for (Entity entity : entities) {
				models.add(getModel(entity));
			}

			return models;
		} catch (Exception e) {
			throw new PersistenceException(e, "");
		}		
	}

}

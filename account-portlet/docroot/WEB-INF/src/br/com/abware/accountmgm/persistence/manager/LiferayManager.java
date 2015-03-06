package br.com.abware.accountmgm.persistence.manager;

import java.util.ArrayList;
import java.util.List;

import br.com.abware.accountmgm.util.BeanUtils;
import br.com.abware.jcondo.core.model.BaseModel;
import br.com.abware.jcondo.exception.PersistenceException;

import com.liferay.faces.portal.context.LiferayPortletHelper;
import com.liferay.faces.portal.context.LiferayPortletHelperImpl;
import com.liferay.portal.model.PersistedModel;

public abstract class LiferayManager<Entity extends PersistedModel, Model extends BaseModel> {

	protected static final long COMPANY = 10153;
	
	protected LiferayPortletHelper helper = new LiferayPortletHelperImpl();
	
	protected abstract Class<Model> getModelClass();

	protected abstract Entity getEntity(Model model) throws Exception;

	public abstract Model save(Model model) throws Exception;

	public abstract void delete(Model model) throws Exception;

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

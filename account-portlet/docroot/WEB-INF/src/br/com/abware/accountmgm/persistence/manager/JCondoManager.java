package br.com.abware.accountmgm.persistence.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.liferay.faces.portal.context.LiferayPortletHelper;
import com.liferay.faces.portal.context.LiferayPortletHelperImpl;

import br.com.abware.accountmgm.persistence.entity.BaseEntity;
import br.com.abware.accountmgm.util.BeanUtils;
import br.com.abware.jcondo.core.model.BaseModel;
import br.com.abware.jcondo.exception.PersistenceException;

public abstract class JCondoManager<Entity extends BaseEntity, Model extends BaseModel> {

	protected static EntityManagerFactory emf = Persistence.createEntityManagerFactory("account-portlet");

	protected EntityManager em;

	protected String emOwner;
	
	protected LiferayPortletHelper helper = new LiferayPortletHelperImpl();	

	protected abstract Class<Model> getModelClass();

	protected abstract Class<Entity> getEntityClass();

	protected Entity getEntity(Model model) throws Exception {
		Entity entity = getEntityClass().newInstance();
		BeanUtils.copyProperties(entity, model);
		return entity;
	}
	
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

	public synchronized void openManager(String owner) {
		if (em == null || !em.isOpen()) {
			em = emf.createEntityManager();
			emOwner = owner;
		}
	}

	public synchronized void closeManager(String owner) {
		if (em != null && em.isOpen()) {
			if (owner != null && owner.equals(emOwner)) {
				em.close();
				emOwner = null;
			}
		}
	}

	public Model save(Model model) throws Exception {
		String key = generateKey();
		Date date = new Date();
		Entity entity = getEntity(model);
		entity.setUpdateDate(date);
		entity.setUpdateUser(helper.getUserId());
		try {
			openManager(key);
			em.getTransaction().begin();

			if (em.find(getEntityClass(), entity.getId()) != null) {
				entity = em.merge(entity);
			} else {
				em.persist(entity);
			}

			em.getTransaction().commit();
			em.refresh(entity);

			return getModel(entity);
		} finally {
			closeManager(key);
		}
	}

	public void delete(Model model) throws Exception {
		String key = generateKey();
		try {
			openManager(key);
			Date date = new Date();
			Entity entity = getEntity(model);
			entity.setUpdateDate(date);
			entity.setUpdateUser(helper.getUserId());
	
			em.getTransaction().begin();
			em.remove(entity);
			em.getTransaction().commit();
		} finally {
			closeManager(key);
		}
	}

	public Model findById(Object id) throws Exception {
		String key = generateKey();
		try {
			openManager(key);
			return getModel(em.find(getEntityClass(), id));
		} finally {
			closeManager(key);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Model> findAll() throws Exception {
		String key = generateKey();
		try {
			openManager(key);
			String query = "FROM " + getEntityClass().getSimpleName();
			Query q = em.createQuery(query);
			return getModels(q.getResultList());
		} finally {
			closeManager(key);
		}
	}
	
	protected String generateKey() {
		return UUID.randomUUID().toString();
	}

}

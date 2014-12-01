package br.com.abware.complaintbook.persistence.manager;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Persistence;
import javax.persistence.Query;

import br.com.abware.complaintbook.persistence.entity.BaseEntity;

public abstract class BaseManager<Entity extends BaseEntity> {

	protected EntityManagerFactory emf = Persistence.createEntityManagerFactory("book-portlet");
	
	protected EntityManager em;

	protected String emOwner;

	protected abstract Class<Entity> getEntityClass();

	public void openManager(String owner) {
		if (em == null || !em.isOpen()) {
			em = emf.createEntityManager();
			emOwner = owner;
		}
	}

	public void closeManager(String owner) {
		if (em != null && em.isOpen()) {
			if (owner != null && owner.equals(emOwner)) {
				em.close();
				emOwner = null;
			}
		}
	}

	public Entity save(Entity entity, long agentId) {
		Date currentDate = new Date();
		entity.setAgentId(agentId);

		try {
			em.getTransaction().begin();

			if (findById(entity.getId()) != null) {
				entity.setModifiedDate(currentDate);
				em.merge(entity);
			} else {
				entity.setCreateDate(currentDate);
				entity.setModifiedDate(currentDate);
				em.persist(entity);
			}

			em.getTransaction().commit();
			em.refresh(entity);
		} catch (Exception e) {
			em.getTransaction().rollback();
		}

		return entity;
	}
	
	public void delete(Entity entity, long agentId) {
		em.getTransaction().begin();
		em.remove(entity);
		em.getTransaction().commit();
	}

	public Entity findById(Object id) {
		return em.find(getEntityClass(), id);
	}

	@SuppressWarnings("unchecked")
	public List<Entity> findAll() {
		String query = "FROM " + getEntityClass().getSimpleName();
		Query q = em.createQuery(query);
		return q.getResultList();
	}	
}

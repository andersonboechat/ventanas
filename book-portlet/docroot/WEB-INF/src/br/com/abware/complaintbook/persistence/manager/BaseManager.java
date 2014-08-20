package br.com.abware.complaintbook.persistence.manager;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

public abstract class BaseManager<Entity extends Serializable> {

	protected static EntityManagerFactory emf = Persistence.createEntityManagerFactory("ComplaintBook-portlet");
	
	protected abstract Class<Entity> getEntityClass();
	
	public Entity save(Entity entity) throws Exception {
		EntityManager em = null; 
		
		try {
			em = emf.createEntityManager();

			em.getTransaction().begin();
			
			if (em.contains(entity)) {
				em.merge(entity);
			} else {
				em.persist(entity);
			}
			
			em.getTransaction().commit();
			em.refresh(entity);
	
			return entity;
		} catch (Exception e) {
			em.getTransaction().rollback();
			throw e;
		} finally {
			if (em != null) {
				em.close();
			}
		}
	}

	public Entity findById(Object id) {
		EntityManager em = null; 
		
		try {
			em = emf.createEntityManager();
			return em.find(getEntityClass(), id);
		} finally {
			if (em != null) {
				em.close();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public List<Entity> findAll() {
		EntityManager em = null; 
		
		try {
			em = emf.createEntityManager();
				
			String query = "FROM " + getEntityClass().getSimpleName();
	
			Query q = em.createQuery(query);
			
			return q.getResultList();
		} finally {
			if (em != null) {
				em.close();
			}
		}
	}

}

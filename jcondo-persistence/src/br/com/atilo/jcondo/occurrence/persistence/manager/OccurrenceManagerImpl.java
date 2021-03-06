package br.com.atilo.jcondo.occurrence.persistence.manager;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.crm.model.Occurrence;
import br.com.atilo.jcondo.core.persistence.manager.JCondoManager;
import br.com.atilo.jcondo.core.persistence.manager.PersonManagerImpl;
import br.com.atilo.jcondo.occurrence.persistence.entity.OccurrenceEntity;

public class OccurrenceManagerImpl extends JCondoManager<OccurrenceEntity, Occurrence> {

	protected PersonManagerImpl personManager = new PersonManagerImpl();

	@Override
	protected Class<Occurrence> getModelClass() {
		return Occurrence.class;
	}

	@Override
	protected Class<OccurrenceEntity> getEntityClass() {
		return OccurrenceEntity.class;
	}

	@Override
	protected Occurrence getModel(OccurrenceEntity entity) throws Exception {
		if (entity == null) {
			return null;
		}

		Occurrence occurrence = super.getModel(entity);
		occurrence.setPerson(personManager.findById(entity.getPerson().getId()));

		if (occurrence.getAnswer() != null) {
			occurrence.getAnswer().setPerson(personManager.findById(entity.getAnswer().getPerson().getId()));	
		}

		return occurrence;
	}

	@SuppressWarnings("unchecked")
	public List<Occurrence> findOccurrences(Person person) throws Exception {
		String key = generateKey();
		try {
			openManager(key);
			String queryString = "FROM OccurrenceEntity WHERE person.id = :personId ORDER BY date DESC";
			Query query = em.createQuery(queryString);
			query.setParameter("personId", person.getId());
			return getModels(query.getResultList());
		} catch (NoResultException e) {
			return new ArrayList<Occurrence>();
		} finally {
			closeManager(key);
		}			
	}

	public Occurrence findOccurrence(String code) throws Exception {
		String key = generateKey();
		try {
			openManager(key);
			String queryString = "FROM OccurrenceEntity WHERE code = :code ORDER BY date DESC";
			Query query = em.createQuery(queryString);
			query.setParameter("code", code);
			return getModel((OccurrenceEntity) query.getSingleResult());
		} catch (NoResultException e) {
			return null;
		} finally {
			closeManager(key);
		}			
	}
}

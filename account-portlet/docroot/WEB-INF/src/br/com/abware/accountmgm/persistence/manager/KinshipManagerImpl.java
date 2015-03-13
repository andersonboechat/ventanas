package br.com.abware.accountmgm.persistence.manager;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import br.com.abware.accountmgm.persistence.entity.KinshipEntity;
import br.com.abware.jcondo.core.model.Kinship;
import br.com.abware.jcondo.core.model.Person;

public class KinshipManagerImpl extends JCondoManager<KinshipEntity, Kinship> {

	private NewPersonManagerImpl personManager = new NewPersonManagerImpl();
	
	@Override
	protected Class<Kinship> getModelClass() {
		return Kinship.class;
	}

	@Override
	protected Class<KinshipEntity> getEntityClass() {
		return KinshipEntity.class;
	}

	@Override
	protected Kinship getModel(KinshipEntity entity) throws Exception {
		Kinship kinship = super.getModel(entity);
		kinship.setPerson(personManager.getModel(entity.getPerson()));
		kinship.setRelative(personManager.getModel(entity.getRelative()));
		return kinship;
	}
	
	@SuppressWarnings("unchecked")
	public List<Kinship> findByPerson(Person person) throws Exception {
		String key = generateKey();
		try {
			openManager(key);
			String queryString = "FROM KinshipEntity WHERE person.id = :id";
			Query query = em.createQuery(queryString);
			query.setParameter("id", person.getId());
			return getModels(query.getResultList());
		} catch (NoResultException e) {
			return new ArrayList<Kinship>();
		} finally {
			closeManager(key);
		}
	}
}

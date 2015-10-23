package br.com.atilo.jcondo.core.persistence.manager;

import java.util.List;

import javax.persistence.Query;

import br.com.abware.jcondo.core.model.Domain;
import br.com.abware.jcondo.core.model.Membership;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.exception.PersistenceException;
import br.com.atilo.jcondo.core.persistence.entity.MembershipEntity;

public class MembershipManagerImpl extends JCondoManager<MembershipEntity, Membership> {

	@Override
	protected Class<Membership> getModelClass() {
		return Membership.class;
	}

	@Override
	protected Class<MembershipEntity> getEntityClass() {
		return MembershipEntity.class;
	}

	@SuppressWarnings("unchecked")
	public List<Membership> findByPerson(Person person) throws Exception {
		String key = generateKey();
		String queryString = "FROM MembershipEntity WHERE personId = :id";

		try {
			openManager(key);
			Query query = em.createQuery(queryString);
			query.setParameter("id", person.getId());
			return getModels(query.getResultList());
		} catch (Exception e) {
			throw new PersistenceException(e, "psn.mgr.find.person.fail");
		} finally {
			closeManager(key);
		}
	}

	public Membership findByPersonAndDomain(Person person, Domain domain) {
		// TODO Auto-generated method stub
		return null;
	}
}

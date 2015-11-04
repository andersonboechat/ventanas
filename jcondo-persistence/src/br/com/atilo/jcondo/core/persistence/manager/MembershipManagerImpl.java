package br.com.atilo.jcondo.core.persistence.manager;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import br.com.abware.jcondo.core.model.Domain;
import br.com.abware.jcondo.core.model.Membership;
import br.com.abware.jcondo.core.model.Person;
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
		} catch (NoResultException e) {
			return new ArrayList<Membership>();
		} finally {
			closeManager(key);
		}
	}

	public Membership findByPersonAndDomain(Person person, Domain domain) throws Exception {
		String key = generateKey();
		String queryString = "FROM MembershipEntity WHERE personId = :personId AND domainId = :domainId";

		try {
			openManager(key);
			Query query = em.createQuery(queryString);
			query.setParameter("personId", person.getId());
			query.setParameter("domainId", domain.getId());
			return getModel((MembershipEntity) query.getSingleResult());
		} catch (NoResultException e) {
			return null;
		} finally {
			closeManager(key);
		}
	}
}

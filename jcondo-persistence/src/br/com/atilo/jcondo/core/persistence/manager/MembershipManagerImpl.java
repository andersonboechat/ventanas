package br.com.atilo.jcondo.core.persistence.manager;

import java.util.List;

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

	public List<Membership> findByPerson(Person person) {
		return null;
	}
}

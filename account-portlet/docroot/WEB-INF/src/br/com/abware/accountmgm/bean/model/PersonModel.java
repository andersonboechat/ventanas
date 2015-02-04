package br.com.abware.accountmgm.bean.model;

import java.util.List;

import br.com.abware.jcondo.core.model.BaseModel;
import br.com.abware.jcondo.core.model.Membership;
import br.com.abware.jcondo.core.model.Person;

public class PersonModel implements BaseModel {

	private Person person;
	
	private List<Membership> memberships;

	public PersonModel(Person person, List<Membership> memberships) {
		this.person = person;
		this.memberships = memberships;
	}
	
	@Override
	public boolean equals(Object obj) {
		return super.equals(obj) || ((PersonModel) obj).getId() == getId();
	}
	
	@Override
	public long getId() {
		return person.getId();
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public List<Membership> getMemberships() {
		return memberships;
	}

	public void setMemberships(List<Membership> memberships) {
		this.memberships = memberships;
	}

}

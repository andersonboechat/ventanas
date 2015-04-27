package br.com.atilo.jcondo.core.persistence.entity;

import javax.persistence.*;

import br.com.abware.jcondo.core.PersonType;


/**
 * The persistent class for the jco_membership database table.
 * 
 */
@Entity
@Table(name="jco_membership")
public class MembershipEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private long id;

	@OneToOne
	@JoinColumn(name="domainId")
	private DomainEntity domain;

	@Enumerated(EnumType.ORDINAL)
	private PersonType type;

	@ManyToOne
	@JoinColumn(name="personId")
	private PersonEntity person;
	
	public MembershipEntity() {
	}	

	public MembershipEntity(PersonEntity person) {
		this.person = person;
	}	

	public void setId(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public DomainEntity getDomain() {
		return domain;
	}

	public void setDomain(DomainEntity domain) {
		this.domain = domain;
	}

	public PersonType getType() {
		return this.type;
	}

	public void setType(PersonType type) {
		this.type = type;
	}

	public PersonEntity getPerson() {
		return person;
	}

	public void setPerson(PersonEntity person) {
		this.person = person;
	}

}
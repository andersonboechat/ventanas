package br.com.abware.accountmgm.persistence.entity;

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

	private long personId;
	
	public MembershipEntity() {
	}	

	public MembershipEntity(long personId) {
		this.personId = personId;
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

	public long getPersonId() {
		return personId;
	}

	public void setPersonId(long personId) {
		this.personId = personId;
	}
}
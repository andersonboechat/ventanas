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

	@OneToOne
	@JoinColumn(name="personId")
	private PersonEntity person;

	@Enumerated(EnumType.ORDINAL)
	private PersonType type;

	public MembershipEntity() {
	}

	public long getId() {
		return id;
	}

	public PersonType getType() {
		return this.type;
	}

	public void setType(PersonType type) {
		this.type = type;
	}

}
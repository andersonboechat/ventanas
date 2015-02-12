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

	@EmbeddedId
	private MembershipEntityPK membershipId;

	@Enumerated(EnumType.ORDINAL)
	private PersonType type;

	public MembershipEntity() {
	}

	public long getId() {
		return 0;
	}

	public MembershipEntityPK getMembershipId() {
		return this.membershipId;
	}

	public void setMembershipId(MembershipEntityPK id) {
		this.membershipId = id;
	}

	public PersonType getType() {
		return this.type;
	}

	public void setType(PersonType type) {
		this.type = type;
	}

}
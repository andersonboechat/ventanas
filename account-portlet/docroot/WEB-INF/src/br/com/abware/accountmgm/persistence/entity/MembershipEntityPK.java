package br.com.abware.accountmgm.persistence.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the jco_membership database table.
 * 
 */
@Embeddable
public class MembershipEntityPK implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long domainId;

	private Long personId;

	public Long getDomainId() {
		return this.domainId;
	}

	public void setDomainId(Long domainId) {
		this.domainId = domainId;
	}

	public Long getPersonId() {
		return this.personId;
	}

	public void setPersonId(Long personId) {
		this.personId = personId;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof MembershipEntityPK)) {
			return false;
		}
		MembershipEntityPK castOther = (MembershipEntityPK)other;
		return 
			this.domainId.equals(castOther.domainId)
			&& this.personId.equals(castOther.personId);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.domainId.hashCode();
		hash = hash * prime + this.personId.hashCode();
		
		return hash;
	}
}
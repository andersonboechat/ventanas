package br.com.abware.accountmgm.persistence.entity;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

@Entity
@Table(name="jco_domain")
@Inheritance(strategy=InheritanceType.JOINED)
@DiscriminatorColumn(name="domain", discriminatorType=DiscriminatorType.INTEGER)
public class DomainEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private long id;
	
	private long relatedId;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getRelatedId() {
		return relatedId;
	}

	public void setRelatedId(long relatedId) {
		this.relatedId = relatedId;
	}
	
}

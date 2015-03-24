package br.com.atilo.jcondo.core.persistence.entity;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="jco_domain")
@Inheritance(strategy=InheritanceType.JOINED)
@DiscriminatorColumn(name="domain", discriminatorType=DiscriminatorType.INTEGER)
public abstract class DomainEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private long id;

	@OneToOne
	@JoinColumn(name="parentId", nullable=true)	
	private DomainEntity parent;

	private long relatedId;

	@Column(updatable=false)
	private long folderId;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public DomainEntity getParent() {
		return parent;
	}

	public void setParent(DomainEntity parent) {
		this.parent = parent;
	}

	public long getRelatedId() {
		return relatedId;
	}

	public void setRelatedId(long relatedId) {
		this.relatedId = relatedId;
	}

	public long getFolderId() {
		return folderId;
	}

	public void setFolderId(long folderId) {
		this.folderId = folderId;
	}
	
}

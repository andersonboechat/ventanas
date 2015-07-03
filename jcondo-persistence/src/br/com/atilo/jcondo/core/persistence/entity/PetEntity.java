package br.com.atilo.jcondo.core.persistence.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import br.com.abware.jcondo.core.model.PetType;


/**
 * The persistent class for the jco_pet database table.
 * 
 */
@Entity
@Table(name="jco_pet")
public class PetEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Id
	private long id;

	@Enumerated(EnumType.ORDINAL)
	private PetType type;

	private String description;

	@OneToOne
	@JoinColumn(name="domainId")
	private DomainEntity domain;

	private long imageId;

	public PetEntity() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public PetType getType() {
		return type;
	}

	public void setType(PetType type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public DomainEntity getDomain() {
		return domain;
	}

	public void setDomain(DomainEntity domain) {
		this.domain = domain;
	}

	public long getImageId() {
		return imageId;
	}

	public void setImageId(long imageId) {
		this.imageId = imageId;
	}

}
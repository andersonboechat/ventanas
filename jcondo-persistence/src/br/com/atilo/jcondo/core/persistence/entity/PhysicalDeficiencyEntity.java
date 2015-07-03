package br.com.atilo.jcondo.core.persistence.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.com.abware.jcondo.core.model.DeficiencyType;

/**
 * The persistent class for the jco_person_deficiency database table.
 * 
 */
@Entity
@Table(name="jco_person_deficiency")
public class PhysicalDeficiencyEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Id
	private long id;

	private String description;

	@Enumerated(EnumType.ORDINAL)
	private DeficiencyType type;

	@ManyToOne
	@JoinColumn(name="personId")
	private PersonEntity person;

	public PhysicalDeficiencyEntity() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public DeficiencyType getType() {
		return type;
	}

	public void setType(DeficiencyType type) {
		this.type = type;
	}

	public PersonEntity getPerson() {
		return person;
	}

	public void setPerson(PersonEntity person) {
		this.person = person;
	}

}
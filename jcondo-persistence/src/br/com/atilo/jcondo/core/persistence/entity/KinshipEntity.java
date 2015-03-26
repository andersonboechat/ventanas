package br.com.atilo.jcondo.core.persistence.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import br.com.abware.jcondo.core.model.KinType;



/**
 * The persistent class for the jco_kinship database table.
 * 
 */
@Entity
@Table(name="jco_kinship")
public class KinshipEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private long id;

	@OneToOne
	@JoinColumn(name="personId")
	private PersonEntity person;

	@OneToOne
	@JoinColumn(name="relativeId")
	private PersonEntity relative;

	@Enumerated(EnumType.ORDINAL)
	private KinType type;

	public KinshipEntity() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public PersonEntity getPerson() {
		return person;
	}

	public void setPerson(PersonEntity person) {
		this.person = person;
	}

	public PersonEntity getRelative() {
		return relative;
	}

	public void setRelative(PersonEntity relative) {
		this.relative = relative;
	}

	public KinType getType() {
		return type;
	}

	public void setType(KinType type) {
		this.type = type;
	}
	

}
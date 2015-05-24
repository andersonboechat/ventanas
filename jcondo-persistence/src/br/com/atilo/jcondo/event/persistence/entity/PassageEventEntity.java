package br.com.atilo.jcondo.event.persistence.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import br.com.abware.jcondo.access.model.PassageType;
import br.com.atilo.jcondo.core.persistence.entity.DomainEntity;
import br.com.atilo.jcondo.core.persistence.entity.PersonEntity;
import br.com.atilo.jcondo.core.persistence.entity.VehicleEntity;


/**
 * The persistent class for the jco_passage_event database table.
 * 
 */
@Entity
@Table(name="jco_passage_event")
public class PassageEventEntity extends EventEntity {

	private static final long serialVersionUID = 1L;

	@Enumerated(EnumType.ORDINAL)
	private PassageType type;

	@OneToOne
	@JoinColumn(name="personId", nullable=true)
	private PersonEntity person;

	@OneToOne
	@JoinColumn(name="vehicleId", nullable=true)
	private VehicleEntity vehicle;

	@OneToOne
	@JoinColumn(name="domainId", nullable=true)
	private DomainEntity domain;

	@OneToOne
	@JoinColumn(name="authorizerId", nullable=true)
	private PersonEntity authorizer;

	public PassageEventEntity() {
	}

	public PassageType getType() {
		return type;
	}

	public void setType(PassageType type) {
		this.type = type;
	}

	public PersonEntity getPerson() {
		return person;
	}

	public void setPerson(PersonEntity person) {
		this.person = person;
	}

	public VehicleEntity getVehicle() {
		return vehicle;
	}

	public void setVehicle(VehicleEntity vehicle) {
		this.vehicle = vehicle;
	}

	public DomainEntity getDomain() {
		return domain;
	}

	public void setDomain(DomainEntity domain) {
		this.domain = domain;
	}

	public PersonEntity getAuthorizer() {
		return authorizer;
	}

	public void setAuthorizer(PersonEntity authorizer) {
		this.authorizer = authorizer;
	}



}
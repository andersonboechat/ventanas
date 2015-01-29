package br.com.abware.accountmgm.persistence.entity;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import br.com.abware.accountmgm.model.ParkingType;


/**
 * The persistent class for the jco_parking database table.
 * 
 */
@Entity
@Table(name="jco_parking")
@Inheritance(strategy=InheritanceType.JOINED)
@DiscriminatorColumn(name="ID", discriminatorType=DiscriminatorType.INTEGER)
public class ParkingEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private long id;

	private String code;

	private long domainId;

	@Enumerated(EnumType.ORDINAL)
	private ParkingType type;

	public ParkingEntity() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public long getDomainId() {
		return this.domainId;
	}

	public void setDomainId(long domainId) {
		this.domainId = domainId;
	}

	public ParkingType getType() {
		return this.type;
	}

	public void setType(ParkingType type) {
		this.type = type;
	}

}
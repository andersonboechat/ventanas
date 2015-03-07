package br.com.abware.accountmgm.persistence.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import br.com.abware.accountmgm.model.ParkingType;


/**
 * The persistent class for the jco_parking database table.
 * 
 */
@Entity
@Table(name="jco_parking")
public class ParkingEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private long id;

	private String code;

	@OneToOne
	@JoinColumn(name="ownerDomainId", nullable=true)
	private DomainEntity ownerDomain;

	@OneToOne
	@JoinColumn(name="renterDomainId", nullable=true)
	private DomainEntity renterDomain;

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

	public DomainEntity getOwnerDomain() {
		return ownerDomain;
	}

	public void setOwnerDomain(DomainEntity ownerDomain) {
		this.ownerDomain = ownerDomain;
	}

	public DomainEntity getRenterDomain() {
		return renterDomain;
	}

	public void setRenterDomain(DomainEntity renterDomain) {
		this.renterDomain = renterDomain;
	}

	public ParkingType getType() {
		return this.type;
	}

	public void setType(ParkingType type) {
		this.type = type;
	}

}
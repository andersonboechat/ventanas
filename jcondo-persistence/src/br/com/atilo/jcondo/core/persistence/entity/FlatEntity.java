package br.com.atilo.jcondo.core.persistence.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="jco_flat")
@DiscriminatorValue("2")
public class FlatEntity extends DomainEntity {

	private static final long serialVersionUID = 1L;

	@Column(updatable=false)
	private int block;

	@Column(updatable=false)
	private int number;

	@ManyToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	@JoinTable(name="jco_membership", joinColumns={@JoinColumn(name="domainId", referencedColumnName="id")}, inverseJoinColumns={@JoinColumn(name="personId", referencedColumnName="id")})
	private List<PersonEntity> people;

	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	@JoinColumn(name="ownerDomainId", referencedColumnName="id")
	private List<ParkingEntity> parkings;

	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	@JoinColumn(name="renterDomainId", referencedColumnName="id")
	private List<ParkingEntity> rentedParkings;

	public int getBlock() {
		return block;
	}

	public void setBlock(int block) {
		this.block = block;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public List<ParkingEntity> getParkings() {
		return parkings;
	}

	public void setParkings(List<ParkingEntity> parkings) {
		this.parkings = parkings;
	}

	public List<ParkingEntity> getRentedParkings() {
		return rentedParkings;
	}

	public void setRentedParkings(List<ParkingEntity> rentedParkings) {
		this.rentedParkings = rentedParkings;
	}

}

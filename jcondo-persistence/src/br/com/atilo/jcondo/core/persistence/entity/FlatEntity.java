package br.com.atilo.jcondo.core.persistence.entity;

import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import br.com.abware.jcondo.core.model.PetType;

@Entity
@Table(name="jco_flat")
@DiscriminatorValue("2")
public class FlatEntity extends DomainEntity {

	private static final long serialVersionUID = 1L;

	@OneToOne
	@JoinColumn(name="personId", nullable=true)
	private PersonEntity person;
	
	@Column(updatable=false)
	private int block;

	@Column(updatable=false)
	private int number;

	@Column(nullable=true, columnDefinition="INT(1)")
	private Boolean disables;

	@Column(nullable=true, columnDefinition="INT(1)")
	private Boolean pets;
	
	@ElementCollection(targetClass=PetType.class, fetch=FetchType.EAGER) 
	@CollectionTable(name="jco_flat_pets", joinColumns=@JoinColumn(name="flatId"))
	@Column(name="typeId")
	private List<PetType> petTypes;

	@Column(nullable=true, columnDefinition="INT(1)")
	private Boolean brigade;

    @OneToMany(mappedBy="domain")
    private List<MembershipEntity> memberships;
	
	@OneToMany(fetch=FetchType.LAZY)
	@JoinColumn(name="ownerDomainId", referencedColumnName="id", insertable=false, updatable=false)
	private List<ParkingEntity> parkings;

	@OneToMany(fetch=FetchType.LAZY)
	@JoinColumn(name="renterDomainId", referencedColumnName="id", insertable=false, updatable=false)
	private List<ParkingEntity> rentedParkings;

	public PersonEntity getPerson() {
		return person;
	}

	public void setPerson(PersonEntity person) {
		this.person = person;
	}

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

	public Boolean getDisables() {
		return disables;
	}

	public void setDisables(Boolean disables) {
		this.disables = disables;
	}

	public Boolean getPets() {
		return pets;
	}

	public void setPets(Boolean pets) {
		this.pets = pets;
	}

	public List<PetType> getPetTypes() {
		return petTypes;
	}

	public void setPetTypes(List<PetType> petTypes) {
		this.petTypes = petTypes;
	}

	public Boolean getBrigade() {
		return brigade;
	}

	public void setBrigade(Boolean brigade) {
		this.brigade = brigade;
	}

	public List<MembershipEntity> getMemberships() {
		return memberships;
	}

	public void setMemberships(List<MembershipEntity> memberships) {
		this.memberships = memberships;
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

package br.com.atilo.jcondo.booking.persistence.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import br.com.atilo.jcondo.core.persistence.entity.BaseEntity;


/**
 * The persistent class for the rb_room database table.
 * 
 */
@Entity
@Table(name="jco_resource")
public class RoomEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private long id;
	
	private long folderId;

	private long agreementId;

	@Column(nullable=false, columnDefinition = "BIT", length = 1)
	private boolean available;

	private String detail;

	private String name;

	private double price;
	
	//bi-directional many-to-one association to Booking
	@OneToMany(mappedBy="room")
	private List<BookingEntity> bookings;

	public RoomEntity() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getFolderId() {
		return folderId;
	}

	public void setFolderId(long imageFolderId) {
		this.folderId = imageFolderId;
	}

	public long getAgreementId() {
		return this.agreementId;
	}

	public void setAgreementId(long agreementId) {
		this.agreementId = agreementId;
	}

	public boolean getAvailable() {
		return this.available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public String getDetail() {
		return this.detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public List<BookingEntity> getBookings() {
		return this.bookings;
	}

	public void setBookings(List<BookingEntity> bookings) {
		this.bookings = bookings;
	}

}
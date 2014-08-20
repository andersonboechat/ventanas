package br.com.abware.agenda.persistence.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;


/**
 * The persistent class for the rb_room database table.
 * 
 */
@Entity
@Table(name="rb_room")
public class Room extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private int id;
	
	private long imageFolderId;

	private String agreement;

	@Column(nullable=false, columnDefinition = "BIT", length = 1)
	private boolean available;

	private String detail;

	private String name;

	//bi-directional many-to-one association to Booking
	@OneToMany(mappedBy="room")
	private List<Booking> bookings;

	public Room() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public long getImageFolderId() {
		return imageFolderId;
	}

	public void setImageFolderId(long imageFolderId) {
		this.imageFolderId = imageFolderId;
	}

	public String getAgreement() {
		return this.agreement;
	}

	public void setAgreement(String agreement) {
		this.agreement = agreement;
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

	public List<Booking> getBookings() {
		return this.bookings;
	}

	public void setBookings(List<Booking> bookings) {
		this.bookings = bookings;
	}

}
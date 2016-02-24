package br.com.atilo.jcondo.booking.persistence.entity;

import javax.persistence.*;

import br.com.atilo.jcondo.core.persistence.entity.BaseEntity;

/**
 * The persistent class for the jco_booking_note database table.
 * 
 */
@Entity
@Table(name="jco_booking_note")
public class BookingNoteEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private long id;

	private String text;

	public BookingNoteEntity() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
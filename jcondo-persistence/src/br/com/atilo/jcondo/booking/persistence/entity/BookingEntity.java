package br.com.atilo.jcondo.booking.persistence.entity;

import java.util.Date;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.abware.jcondo.booking.model.BookingStatus;
import br.com.atilo.jcondo.core.persistence.entity.BaseEntity;
import br.com.atilo.jcondo.core.persistence.entity.DomainEntity;
import br.com.atilo.jcondo.core.persistence.entity.PersonEntity;

@MappedSuperclass
public class BookingEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private long id;

	@Temporal(TemporalType.TIMESTAMP)
	private Date beginDate;

	@Temporal(TemporalType.TIMESTAMP)
	private Date endDate;

	@Enumerated(EnumType.ORDINAL)
	private BookingStatus status;

	private double price;

	@OneToOne
	@JoinColumn(name="personId", updatable=false)
	private PersonEntity person;

	@OneToOne
	@JoinColumn(name="domainId")
	private DomainEntity domain;	

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public BookingStatus getStatus() {
		return status;
	}

	public void setStatus(BookingStatus status) {
		this.status = status;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public PersonEntity getPerson() {
		return person;
	}

	public void setPerson(PersonEntity person) {
		this.person = person;
	}

	public DomainEntity getDomain() {
		return domain;
	}

	public void setDomain(DomainEntity domain) {
		this.domain = domain;
	}

}

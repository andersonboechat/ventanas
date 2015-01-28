package br.com.abware.accountmgm.persistence.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.abware.accountmgm.model.AccessType;


/**
 * The persistent class for the jco_access_log database table.
 * 
 */
@Entity
@Table(name="jco_access_log")
@Inheritance(strategy=InheritanceType.JOINED)
@DiscriminatorColumn(name="ID", discriminatorType=DiscriminatorType.INTEGER)
public class AccessLogEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Id
	private long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="id")
	private VehicleEntity vehicle;

	private String comment;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(updatable=false)
	private Date date;

	@Enumerated(EnumType.ORDINAL)
	private AccessType type;

	public AccessLogEntity() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getComment() {
		return this.comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public AccessType getType() {
		return this.type;
	}

	public void setType(AccessType type) {
		this.type = type;
	}

}
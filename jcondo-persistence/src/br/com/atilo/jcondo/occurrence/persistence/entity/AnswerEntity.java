package br.com.atilo.jcondo.occurrence.persistence.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.atilo.jcondo.core.persistence.entity.BaseEntity;
import br.com.atilo.jcondo.core.persistence.entity.PersonEntity;


/**
 * The persistent class for the jco_occurrence_answer database table.
 * 
 */
@Entity
@Table(name="jco_occurrence_answer")
public class AnswerEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private long id;

	private String text;

	@Temporal(TemporalType.TIMESTAMP)
	private Date date;

	@OneToOne
	@JoinColumn(name="personId")
	private PersonEntity person;

	public AnswerEntity() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public PersonEntity getPerson() {
		return person;
	}

	public void setPerson(PersonEntity person) {
		this.person = person;
	}

}
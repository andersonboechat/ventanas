package br.com.atilo.jcondo.occurrence.persistence.entity;

import br.com.abware.jcondo.crm.model.OccurrenceType;
import br.com.atilo.jcondo.core.persistence.entity.BaseEntity;
import br.com.atilo.jcondo.core.persistence.entity.PersonEntity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * The persistent class for the jco_occurrence database table.
 * 
 */
@Entity
@Table(name="jco_occurrence")
public class OccurrenceEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private long id;

	private String code;

	private String text;

	@Enumerated(EnumType.ORDINAL)
	private OccurrenceType type;

	@Temporal(TemporalType.TIMESTAMP)
	private Date date;

	@OneToOne
	@JoinColumn(name="personId", updatable=false)
	private PersonEntity person;

	@OneToOne
	@JoinColumn(name="answerId", updatable=false)
	private AnswerEntity answer;

	public OccurrenceEntity() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public OccurrenceType getType() {
		return type;
	}

	public void setType(OccurrenceType type) {
		this.type = type;
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

	public AnswerEntity getAnswer() {
		return answer;
	}

	public void setAnswer(AnswerEntity answer) {
		this.answer = answer;
	}

}
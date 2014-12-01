package br.com.abware.complaintbook.persistence.entity;

import org.hibernate.validator.constraints.NotEmpty;

import br.com.abware.complaintbook.OccurenceType;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * The persistent class for the cb_occurrence database table.
 * 
 */
@Entity
@Table(name="cb_occurrence")
public class Occurrence extends BaseEntity {
	private static final long serialVersionUID = 1L;

	private String code;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date date;

	@NotEmpty
	private String text;

	@Enumerated(EnumType.ORDINAL)
	private OccurenceType type;

	private long userId;

	@OneToOne
	@JoinColumn(name="answerId")
	private Answer answer;

	public Occurrence(long userId) {
		this.userId = userId;
	}

	public Occurrence() {
		// TODO Auto-generated constructor stub
	}
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public OccurenceType getType() {
		return this.type;
	}

	public void setType(OccurenceType type) {
		this.type = type;
	}

	public long getUserId() {
		return this.userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public Answer getAnswer() {
		return this.answer;
	}

	public void setAnswer(Answer answer) {
		this.answer = answer;
	}

}
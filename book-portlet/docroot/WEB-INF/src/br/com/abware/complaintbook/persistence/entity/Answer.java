package br.com.abware.complaintbook.persistence.entity;

import org.hibernate.validator.constraints.NotEmpty;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * The persistent class for the cb_answer database table.
 * 
 */
@Entity
@Table(name="cb_answer")
public class Answer extends BaseEntity {
	private static final long serialVersionUID = 1L;

	@Temporal(TemporalType.TIMESTAMP)
	private Date date;

	@NotEmpty
	private String text;

	@Column(columnDefinition="bit")
	private boolean draft;

	private long userId;

	public Answer(Date date, String text, long userId) {
		this.date = date;
		this.text = text;
		this.userId = userId;
	}

	public Answer() {
		// TODO Auto-generated constructor stub
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

	public boolean getDraft() {
		return draft;
	}

	public void setDraft(boolean draft) {
		this.draft = draft;
	}

	public long getUserId() {
		return this.userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

}
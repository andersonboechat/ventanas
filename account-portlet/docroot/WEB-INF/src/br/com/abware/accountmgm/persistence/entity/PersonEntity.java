package br.com.abware.accountmgm.persistence.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="jco_person")
public class PersonEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private long id;

	private long userId;

    private String identity;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

}

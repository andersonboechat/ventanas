package br.com.abware.accountmgm.persistence.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@MappedSuperclass
public abstract class BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Temporal(TemporalType.TIMESTAMP)
	protected Date updateDate;	

	protected long updateUser;

	public abstract long getId();

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date date) {
		this.updateDate = date;
	}

	public long getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(long personId) {
		this.updateUser = personId;
	}

}

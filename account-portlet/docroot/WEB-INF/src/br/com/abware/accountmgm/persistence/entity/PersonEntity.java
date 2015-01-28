package br.com.abware.accountmgm.persistence.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name="jco_person")
public class PersonEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Id
	private long id;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

}

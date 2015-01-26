package br.com.abware.accountmgm.persistence.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="Organization_")
public class FlatVO {

	@Id
	@Column(name="organizationId", insertable=false, updatable=false)
	private long id;

	private String name;

	private transient long block;

	private transient long number;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getBlock() {
		if (block == 0) {
			block = Long.parseLong(name.split("/")[0]); 
		}
		return block;
	}

	public void setBlock(long block) {
		this.block = block;
	}

	public long getNumber() {
		if (number == 0) {
			number = Long.parseLong(name.split("/")[1]); 
		}
		return number;
	}

	public void setNumber(long number) {
		this.number = number;
	}

}

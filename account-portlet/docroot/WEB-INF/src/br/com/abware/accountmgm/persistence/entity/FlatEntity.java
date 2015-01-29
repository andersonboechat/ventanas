package br.com.abware.accountmgm.persistence.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="jco_flat")
public class FlatEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private long id;

	@Column(updatable=false)
	private int block;

	@Column(updatable=false)
	private int number;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getBlock() {
		return block;
	}

	public void setBlock(int block) {
		this.block = block;
	}

	public long getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

}

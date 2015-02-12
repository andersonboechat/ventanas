package br.com.abware.accountmgm.persistence.entity;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="jco_flat")
@DiscriminatorValue(value="1")
public class FlatEntity extends DomainEntity {

	private static final long serialVersionUID = 1L;

	@Column(updatable=false)
	private int block;

	@Column(updatable=false)
	private int number;

	public int getBlock() {
		return block;
	}

	public void setBlock(int block) {
		this.block = block;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

}

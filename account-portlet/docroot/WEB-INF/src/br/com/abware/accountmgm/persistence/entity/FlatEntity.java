package br.com.abware.accountmgm.persistence.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name="jco_flat")
@DiscriminatorValue("1")
public class FlatEntity extends DomainEntity {

	private static final long serialVersionUID = 1L;

	@Column(updatable=false)
	private int block;

	@Column(updatable=false)
	private int number;

	@ManyToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	@JoinTable(name="jco_membership", joinColumns={@JoinColumn(name="domainId", referencedColumnName="id")}, inverseJoinColumns={@JoinColumn(name="personId", referencedColumnName="id")})
	private List<PersonEntity> people;

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

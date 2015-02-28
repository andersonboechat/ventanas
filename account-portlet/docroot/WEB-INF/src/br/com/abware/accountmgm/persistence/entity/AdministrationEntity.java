package br.com.abware.accountmgm.persistence.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;



/**
 * The persistent class for the jco_administration database table.
 * 
 */
@Entity
@Table(name="jco_administration")
@DiscriminatorValue("1")
public class AdministrationEntity extends DomainEntity {

	private static final long serialVersionUID = 1L;

	private String name;

	public AdministrationEntity() {
	}

	public AdministrationEntity(String name) {
		this();
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
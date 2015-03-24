package br.com.atilo.jcondo.core.persistence.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import br.com.abware.jcondo.core.SupplierStatus;



/**
 * The persistent class for the jco_supplier database table.
 * 
 */
@Entity
@Table(name="jco_supplier")
@DiscriminatorValue("3")
public class SupplierEntity extends DomainEntity {

	private static final long serialVersionUID = 1L;

	private String name;

	private String description;

	private String identity;

	@Enumerated(EnumType.ORDINAL)
	private SupplierStatus status;

	public SupplierEntity() {
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getIdentity() {
		return this.identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	public SupplierStatus getStatus() {
		return this.status;
	}

	public void setStatus(SupplierStatus status) {
		this.status = status;
	}

}
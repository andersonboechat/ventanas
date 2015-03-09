package br.com.abware.accountmgm.model;

import br.com.abware.jcondo.core.model.Domain;
import br.com.abware.jcondo.core.model.Image;

public class Vehicle extends AbstractModel {

	private long id;

	private VehicleType type;
	
	private String license;

	private Image image;

	private Domain domain;

	public boolean equals(Object obj) {
		return super.equals(obj) || (license != null && license.equalsIgnoreCase(((Vehicle) obj).getLicense()));
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("{id:").append(id).append(", license:")
		  .append(license).append(", domain: ").append(domain).append("}");

		return sb.toString();
	}
	
	@Override
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public VehicleType getType() {
		return type;
	}

	public void setType(VehicleType type) {
		this.type = type;
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public Domain getDomain() {
		return domain;
	}

	public void setDomain(Domain domain) {
		this.domain = domain;
	}

}

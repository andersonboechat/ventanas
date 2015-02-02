package br.com.abware.accountmgm.model;

import br.com.abware.jcondo.core.model.BaseModel;

public abstract class AbstractModel implements BaseModel {

	public abstract long getId();

	public boolean equals(Object obj) {
		return super.equals(obj) || (obj != null && obj.getClass() == this.getClass() && ((BaseModel) obj).getId() == getId());
	}

}
package br.com.atilo.jcondo.commons.faces;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.apache.commons.collections.CollectionUtils;
import org.primefaces.component.picklist.PickList;
import org.primefaces.model.DualListModel;

import br.com.abware.jcondo.core.model.BaseModel;
import br.com.atilo.jcondo.commons.collections.IdPredicate;

public class ModelListConverter implements Converter {

	@Override
	@SuppressWarnings("unchecked")
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		PickList list = (PickList) arg1;
		DualListModel<BaseModel> models = (DualListModel<BaseModel>) list.getValue();
		return CollectionUtils.find(CollectionUtils.union(models.getSource(), models.getTarget()), new IdPredicate(Long.valueOf(arg2)));
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object obj) {
		return String.valueOf(((BaseModel) obj).getId());
	}

}

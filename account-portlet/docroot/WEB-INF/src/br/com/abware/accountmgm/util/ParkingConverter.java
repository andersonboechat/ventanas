package br.com.abware.accountmgm.util;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.apache.commons.collections.CollectionUtils;
import org.primefaces.component.picklist.PickList;
import org.primefaces.model.DualListModel;

import br.com.abware.accountmgm.model.Parking;

public class ParkingConverter implements Converter {

	@Override
	@SuppressWarnings("unchecked")
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		PickList list = (PickList) arg1;
		DualListModel<Parking> parkings = (DualListModel<Parking>) list.getValue();
		return CollectionUtils.find(CollectionUtils.union(parkings.getSource(), parkings.getTarget()), new IdPredicate(Long.valueOf(arg2)));
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object obj) {
		return String.valueOf(((Parking) obj).getId());
	}

}

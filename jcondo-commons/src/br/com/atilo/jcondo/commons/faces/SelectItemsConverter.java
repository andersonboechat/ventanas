package br.com.atilo.jcondo.commons.faces;

import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItems;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.apache.commons.collections.CollectionUtils;

import br.com.abware.jcondo.core.model.BaseModel;
import br.com.atilo.jcondo.commons.collections.IdPredicate;

@FacesConverter("selectItemsConverter")
public class SelectItemsConverter implements Converter {

	@Override
	@SuppressWarnings("unchecked")
	public Object getAsObject(FacesContext facescontext, UIComponent uicomponent, String s) {
		if (s != null && !s.equals("0")) {
			List<BaseModel> models = (List<BaseModel>) ((UISelectItems) uicomponent.getChildren().get(1)).getValue();
			return CollectionUtils.find(models, new IdPredicate(Long.parseLong(s)));
		}

		return null;
	}

	@Override
	public String getAsString(FacesContext facescontext, UIComponent uicomponent, Object obj) {
		return obj != null ? String.valueOf(((BaseModel) obj).getId()) : "0";
	}

}

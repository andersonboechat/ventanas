package br.com.atilo.jcondo.commons.faces;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.google.gson.Gson;

@FacesConverter("jsonConverter")
public class JsonConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext facescontext, UIComponent uicomponent, String s) {
		if (s != null && !s.equals("0")) {
			try {
				String className = s.split("#")[0];
				return new Gson().fromJson(s.split("#")[1], Class.forName(className));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	@Override
	public String getAsString(FacesContext facescontext, UIComponent uicomponent, Object obj) {
		return obj != null ? obj.getClass().getName() + "#" + new Gson().toJson(obj) : "0";
	}

}

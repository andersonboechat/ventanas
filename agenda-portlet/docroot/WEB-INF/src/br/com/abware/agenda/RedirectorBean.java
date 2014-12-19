package br.com.abware.agenda;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

@ManagedBean
@RequestScoped
public class RedirectorBean {
	
	public String redirect(String windowState) throws IOException {
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		String context = ec.getRequestContextPath();
		String path = ec.getRequestServletPath();
		FacesContext.getCurrentInstance().getExternalContext().redirect(context + "/views/" + windowState + ".xhtml");
		return windowState;
	}

}

package br.com.abware.accountmgm.bean;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;

import br.com.abware.accountmgm.bean.model.SupplierDataModel;


@ManagedBean
@ViewScoped
public class SupplierBean {

	private static Logger LOGGER = Logger.getLogger(SupplierBean.class);

	private static final SupplierDataModel model = null;
	
	private List<Object> suppliers;

	@PostConstruct
	public void init() {
		
	}

	public List<Object> getSuppliers() {
		return suppliers;
	}

	public void setSuppliers(List<Object> suppliers) {
		this.suppliers = suppliers;
	}
}

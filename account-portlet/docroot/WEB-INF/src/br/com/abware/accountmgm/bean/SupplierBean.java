package br.com.abware.accountmgm.bean;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;

import org.apache.log4j.Logger;

import br.com.abware.accountmgm.bean.model.SupplierDataModel;


@ManagedBean
@ViewScoped
public class SupplierBean {

	private static Logger LOGGER = Logger.getLogger(SupplierBean.class);

	private static final SupplierDataModel model = null;
	
	private Object supplier;
	
	private List<Object> suppliers;

	@PostConstruct
	public void init() {
		
	}
	
	public void onSupplierSave(AjaxBehaviorEvent event) {
		
	}

	public void onSupplierDelete(AjaxBehaviorEvent event) {
		
	}

	public SupplierDataModel getModel() {
		return model;
	}

	public Object getSupplier() {
		return supplier;
	}

	public void setSupplier(Object supplier) {
		this.supplier = supplier;
	}

	public List<Object> getSuppliers() {
		return suppliers;
	}

	public void setSuppliers(List<Object> suppliers) {
		this.suppliers = suppliers;
	}

}

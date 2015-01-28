package br.com.abware.accountmgm.bean;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;

import org.apache.log4j.Logger;

import br.com.abware.accountmgm.bean.model.ModelDataModel;
import br.com.abware.jcondo.core.SupplierStatus;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.core.model.Supplier;


@ManagedBean
@ViewScoped
public class SupplierBean {

	private static Logger LOGGER = Logger.getLogger(SupplierBean.class);

	private static final ModelDataModel<Supplier> model = null;
	
	private Person person;
	
	private Supplier supplier;
	
	private List<Supplier> suppliers;
	
	private List<SupplierStatus> status;

	@PostConstruct
	public void init() {
		try {
			status = Arrays.asList(SupplierStatus.values());
		} catch (Exception e) {
			LOGGER.error("", e);
		}
	}
	
	public void onSupplierSave(AjaxBehaviorEvent event) {
		
	}

	public void onSupplierDelete(AjaxBehaviorEvent event) {
		
	}

	public ModelDataModel<Supplier> getModel() {
		return model;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public List<Supplier> getSuppliers() {
		return suppliers;
	}

	public void setSuppliers(List<Supplier> suppliers) {
		this.suppliers = suppliers;
	}

	public List<SupplierStatus> getStatus() {
		return status;
	}

	public void setStatus(List<SupplierStatus> status) {
		this.status = status;
	}

}

package br.com.abware.accountmgm.bean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;

import org.apache.log4j.Logger;

import br.com.abware.jcondo.core.model.Domain;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Person;

@ManagedBean
@ViewScoped
public class FlatBean extends BaseBean {
	
	private static Logger LOGGER = Logger.getLogger(FlatBean.class);

	@ManagedProperty(value="#{personBean}")
	private PersonBean personBean;

	@ManagedProperty(value="#{vehicleBean}")
	private VehicleBean vehicleBean;	

	@ManagedProperty(value="#{supplierBean}")
	private SupplierBean supplierBean;

	private List<Flat> flats;

	private Flat flat;	

	@PostConstruct
	public void init() {
		try {
			flats = flatService.getFlats(personService.getPerson());
			personBean.init(flats);
			supplierBean.init(flats);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void onFlatSelect() throws Exception {
		personBean.onDomainSearch(flat);
		vehicleBean.onFlatSearch(flat);
		supplierBean.onDomainSearch(flat);
	}
	
	public void onFlatSave() {
		try {
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void onFlatCreate() throws Exception {
	}

	public void onFlatDelete() throws Exception {
	}

	public void onFlatsDelete() throws Exception {
	}

	public PersonBean getPersonBean() {
		return personBean;
	}

	public void setPersonBean(PersonBean personBean) {
		this.personBean = personBean;
	}

	public VehicleBean getVehicleBean() {
		return vehicleBean;
	}

	public void setVehicleBean(VehicleBean vehicleBean) {
		this.vehicleBean = vehicleBean;
	}

	public SupplierBean getSupplierBean() {
		return supplierBean;
	}

	public void setSupplierBean(SupplierBean supplierBean) {
		this.supplierBean = supplierBean;
	}

	public List<Flat> getFlats() {
		return flats;
	}

	public void setFlats(List<Flat> flats) {
		this.flats = flats;
	}

	public Flat getFlat() {
		return flat;
	}

	public void setFlat(Flat flat) {
		this.flat = flat;
	}

}

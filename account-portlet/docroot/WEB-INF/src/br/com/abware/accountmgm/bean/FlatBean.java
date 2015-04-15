package br.com.abware.accountmgm.bean;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;

import br.com.abware.jcondo.core.model.Domain;
import br.com.abware.jcondo.core.model.Flat;

@ManagedBean
@ViewScoped
public class FlatBean extends BaseBean {
	
	private static Logger LOGGER = Logger.getLogger(FlatBean.class);

	@ManagedProperty(value="#{documentBean}")
	private DocumentBean documentBean;

	@ManagedProperty(value="#{personBean}")
	private PersonBean personBean;

	@ManagedProperty(value="#{vehicleBean}")
	private VehicleBean vehicleBean;	

	@ManagedProperty(value="#{supplierBean}")
	private SupplierBean supplierBean;
	
	@ManagedProperty(value="#{parkingBean}")
	private ParkingBean parkingBean;

	private List<Flat> flats;

	private Flat flat;

	@PostConstruct
	public void init() {
		try {
			flats = flatService.getPersonFlats(personService.getPerson());
			vehicleBean.init(flats);
			supplierBean.init(flats);
			documentBean.init(flats);
			parkingBean.init(flats);

			ArrayList<Domain> domains = new ArrayList<Domain>(flats);
			domains.addAll(supplierBean.getSuppliers());

			personBean.init(domains);
			supplierBean.addObserver(personBean);
			vehicleBean.addObserver(parkingBean);
			if (flats.size() == 1) {
				flat = flats.get(0);
			}
		} catch (Exception e) {
			LOGGER.error("", e);
		}
	}

	public void onFlatSelect() throws Exception {
		personBean.onDomainSearch(flat);
		vehicleBean.onDomainSearch(flat);
		supplierBean.onDomainSearch(flat);
		documentBean.onDomainSearch(flat);
		parkingBean.onDomainSearch(flat);
	}
	
	public void onFlatSave() {
		try {
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void onFlatCreate() throws Exception {
		flat = new Flat();
	}

	public void onFlatDelete() throws Exception {
	}

	public void onFlatsDelete() throws Exception {
	}

	public DocumentBean getDocumentBean() {
		return documentBean;
	}

	public void setDocumentBean(DocumentBean documentBean) {
		this.documentBean = documentBean;
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

	public ParkingBean getParkingBean() {
		return parkingBean;
	}

	public void setParkingBean(ParkingBean parkingBean) {
		this.parkingBean = parkingBean;
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

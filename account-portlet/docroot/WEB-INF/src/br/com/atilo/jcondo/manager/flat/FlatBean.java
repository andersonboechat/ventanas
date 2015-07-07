package br.com.atilo.jcondo.manager.flat;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;
import org.apache.myfaces.commons.util.MessageUtils;

import br.com.abware.jcondo.core.model.Flat;
import br.com.atilo.jcondo.core.service.FlatServiceImpl;
import br.com.atilo.jcondo.core.service.PersonServiceImpl;

@ViewScoped
@ManagedBean(name="flatView")
public class FlatBean {

	private static Logger LOGGER = Logger.getLogger(FlatBean.class);

	private final FlatServiceImpl flatService = new FlatServiceImpl();

	private final PersonServiceImpl personService = new PersonServiceImpl();	

	@ManagedProperty(value="#{documentView}")
	private DocumentBean documentBean;

	@ManagedProperty(value="#{personView}")
	private PersonBean personBean;

	@ManagedProperty(value="#{vehicleView}")
	private VehicleBean vehicleBean;	

	@ManagedProperty(value="#{supplierView}")
	private SupplierBean supplierBean;
	
	private List<Flat> flats;

	private Flat flat;

	@PostConstruct
	public void init() {
		try {
			flats = flatService.getPersonFlats(personService.getPerson());
			flat = flats.get(0);
			personBean.init(flat);
			vehicleBean.init(flat);
			supplierBean.init(flat);
			documentBean.init(flat);
		} catch (Exception e) {
			LOGGER.fatal("Failure on flat initialization", e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_FATAL, "general.failure", null);
		}
	}

	public void onFlatSelect() throws Exception {
		personBean.init(flat);
		vehicleBean.init(flat);
		supplierBean.init(flat);
		documentBean.init(flat);
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

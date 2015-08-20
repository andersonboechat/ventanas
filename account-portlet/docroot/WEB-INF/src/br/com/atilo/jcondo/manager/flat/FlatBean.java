package br.com.atilo.jcondo.manager.flat;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;

import org.apache.log4j.Logger;
import org.apache.myfaces.commons.util.MessageUtils;
import org.primefaces.component.selectoneradio.SelectOneRadio;
import org.primefaces.context.RequestContext;

import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.PetType;
import br.com.abware.jcondo.exception.BusinessException;
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
	
	@ManagedProperty(value="#{parkingView}")
	private ParkingBean parkingBean;

	private List<Flat> flats;

	private Flat flat;
	
	private List<PetType> petTypes;

	@PostConstruct
	public void init() {
		try {
			flats = flatService.getPersonFlats(personService.getPerson());
			flat = flats.get(0);
			personBean.init(flat);
			vehicleBean.init(flat);
			documentBean.init(flat);
			parkingBean.init(flat);
			
			petTypes = new ArrayList<PetType>();
			petTypes.add(PetType.DOG);
			petTypes.add(PetType.OTHER);
		} catch (Exception e) {
			LOGGER.fatal("Failure on flat initialization", e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_FATAL, "general.failure", null);
		}
	}

	public void onFlatSelect() throws Exception {
		personBean.init(flat);
		vehicleBean.init(flat);
		documentBean.init(flat);
		parkingBean.init(flat);
	}
	
	public void onFlatSave() {
		try {
			flat = flatService.update(flat);
			MessageUtils.addMessage(FacesMessage.SEVERITY_INFO, "flats.success", null);
		} catch (BusinessException e) {
			LOGGER.warn("Business failure on flat update: " + e.getMessage());
			MessageUtils.addMessage(FacesMessage.SEVERITY_WARN, e.getMessage(), e.getArgs());
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
		} catch (Exception e) {
			LOGGER.error("Failure on flat update", e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_FATAL, "general.failure", null);
		}
	}

	public void onFlatCreate() throws Exception {
		flat = new Flat();
	}

	public void onFlatDelete() throws Exception {
	}

	public void onFlatsDelete() throws Exception {
	}

	public void onOptionSelect(AjaxBehaviorEvent event) {
		Boolean value = (Boolean) ((SelectOneRadio) event.getSource()).getValue();
		RequestContext.getCurrentInstance().addCallbackParam("value", value);
	}	
	
	public List<Flat> showFlats() {
		List<Flat> flats = null;
		try {
			flats = flatService.getFlats();
			flats.remove(flat);
		} catch (Exception e) {
			LOGGER.error("Failure on getting all flats", e);
		}
		return flats;
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

	public List<PetType> getPetTypes() {
		return petTypes;
	}

	public void setPetTypes(List<PetType> petTypes) {
		this.petTypes = petTypes;
	}

}

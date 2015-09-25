package br.com.atilo.jcondo.manager.flat;

import java.util.Arrays;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.myfaces.commons.util.MessageUtils;
import org.primefaces.context.RequestContext;

import br.com.abware.accountmgm.util.DomainPredicate;
import br.com.abware.jcondo.core.Gender;
import br.com.abware.jcondo.core.PersonType;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.KinType;
import br.com.abware.jcondo.core.model.Kinship;
import br.com.abware.jcondo.core.model.Membership;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.core.model.Phone;
import br.com.abware.jcondo.core.model.PhoneType;
import br.com.abware.jcondo.exception.BusinessException;
import br.com.abware.jcondo.exception.ModelExistException;
import br.com.atilo.jcondo.core.service.KinshipServiceImpl;
import br.com.atilo.jcondo.core.service.MembershipServiceImpl;
import br.com.atilo.jcondo.core.service.PersonServiceImpl;
import br.com.atilo.jcondo.core.service.PhoneServiceImpl;
import br.com.atilo.jcondo.manager.CameraBean;
import br.com.atilo.jcondo.manager.ImageUploadBean;


@ViewScoped
@ManagedBean(name="newPersonView")
public class NewPersonBean {

	private static Logger LOGGER = Logger.getLogger(NewPersonBean.class);

	private PersonServiceImpl personService = new PersonServiceImpl();
	
	private MembershipServiceImpl membershipService = new MembershipServiceImpl();
	
	private KinshipServiceImpl kinshipService = new KinshipServiceImpl();
	
	private PhoneServiceImpl phoneService = new PhoneServiceImpl();
	
	private ImageUploadBean imageUploadBean;
	
	private CameraBean cameraBean;

	private Flat flat;

	private Person person;

	private Person logPerson;
	
	private PersonType personType;
	
	private List<PersonType> personTypes;

	private List<Gender> genders;

	private Phone phone;

	private String phoneNumber;

	private PhoneType phoneType;

	private List<PhoneType> phoneTypes;

	private KinType kinType;

	private List<KinType> kinTypes;

	public NewPersonBean() {
		imageUploadBean = new ImageUploadBean(158, 240);
		cameraBean = new CameraBean(158, 240);
		genders = Arrays.asList(Gender.values());
		phoneTypes = Arrays.asList(PhoneType.values());
		kinTypes = new ArrayList<KinType>();
		kinTypes.add(KinType.SPOUSE);
		kinTypes.add(KinType.PARENT);
		kinTypes.add(KinType.CHILD);
		kinTypes.add(KinType.OTHER);
	}
	
	public void init(Person person, Flat flat) {
		try {
			this.flat = flat;
			if (person.getId() == 0) {
				this.person = person;
			} else {
				BeanUtils.copyProperties(this.person, person);
			}
			logPerson = personService.getPerson();
			personTypes = personService.getTypes(flat);

			imageUploadBean.setImage(person.getPicture());
			Membership membership = membershipService.getMembership(person, flat);
			if (membership != null) {
				personType = membership.getType();
			}

			phone =  phoneService.getPhone(person);
			if (phone != null) {
				phoneNumber = phone.getExtension() + phone.getNumber();
				phoneType = phone.getType();
			} else {
				phone = new Phone();
			}

			Kinship kinship = kinshipService.getKinship(logPerson, person);
			if (kinship != null) {
				kinType = kinship.getType();
			}
		} catch (Exception e) {
			LOGGER.fatal("Failure on person initialization", e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_FATAL, "general.failure", null);
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
		}
	}

	public void onSearch() throws Exception {
		Person p = personService.getPerson(person.getIdentity());
		if (p != null) {
			init(p, flat);
		}
	}
	
	public void onSave() {
		try {
			if (person.getId() == 0) {
				person = personService.add(person.getIdentity(), person.getFirstName(), 
										   person.getLastName(), person.getGender(), 
										   person.getBirthday(), person.getEmailAddress(), 
										   null, flat, personType, kinType);
				MessageUtils.addMessage(FacesMessage.SEVERITY_INFO, "flats.user.add.success", null);
			} else {
				person = personService.update(person.getId(), person.getIdentity(),  
											  person.getFirstName(),person.getLastName(), 
											  person.getGender(), person.getBirthday(), 
											  person.getEmailAddress(), null, flat, 
											  personType, kinType);
				MessageUtils.addMessage(FacesMessage.SEVERITY_INFO, "flats.user.update.success", null);
			}

			if (StringUtils.isEmpty(phoneNumber)) {
				if (phone.getId() > 0) {
					phoneService.remove(phone.getId());
				}
			} else {
				String pn = phoneNumber.replaceAll("[^0-9]+", "");

				if (!phoneNumber.equals(phone.getExtension() + phone.getNumber()) || 
						phoneType != phone.getType()) {
					if (phone.getId() == 0) {
						phoneService.add(logPerson, pn.substring(0, 2), pn.substring(2), 
										 phoneType, true);
					} else {
						phoneService.update(phone.getId(), pn.substring(0, 2), pn.substring(2), 
											phoneType, true);
					}
				}
			}

		} catch (ModelExistException e) {
			MessageUtils.addMessage(FacesMessage.SEVERITY_WARN, e.getMessage(), e.getArgs(), ":tabs:person-details-form:alertMsg");
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
			RequestContext.getCurrentInstance().addCallbackParam("alert", true);
		} catch (BusinessException e) {
			LOGGER.warn("Business failure on person saving: " + e.getMessage());
			MessageUtils.addMessage(FacesMessage.SEVERITY_WARN, e.getMessage(), e.getArgs());
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
		} catch (Exception e) {
			LOGGER.error("Unexpected failure on person saving", e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_ERROR, "general.failure", null);
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
		}	
	}

	public boolean canChangeType() throws Exception {
		if (person.getId() == 0) {
			return true;
		}

		Membership membership = (Membership) CollectionUtils.find(person.getMemberships(), new DomainPredicate(flat));

		return membership != null && personTypes != null ? personTypes.contains(membership.getType()) : false;
	}

	public boolean canEditPerson(Person person) throws Exception {
		boolean canEdit = false;

		if (person.getId() == 0 || person.equals(personService.getPerson())) {
			canEdit = true;
		} else {
			Membership membership = (Membership) CollectionUtils.find(person.getMemberships(), new DomainPredicate(flat));
			canEdit = membership != null && personTypes != null ? personTypes.contains(membership.getType()) : false;
		}

		return canEdit;
	}

	public boolean canEditInfo() throws Exception {
		return person != null && (person.getId() == 0 || person.equals(personService.getPerson()));
	}

	public void validatePhone(FacesContext context, UIComponent component, Object value) {
		if (!StringUtils.isEmpty((String) value) && phone.getType() == null) {
			FacesMessage message = MessageUtils.getMessage(UIInput.REQUIRED_MESSAGE_ID, null);
			throw new ValidatorException(message);
		}
	}

	public PhoneServiceImpl getPhoneService() {
		return phoneService;
	}

	public void setPhoneService(PhoneServiceImpl phoneService) {
		this.phoneService = phoneService;
	}

	public ImageUploadBean getImageUploadBean() {
		return imageUploadBean;
	}

	public void setImageUploadBean(ImageUploadBean imageUploadBean) {
		this.imageUploadBean = imageUploadBean;
	}

	public CameraBean getCameraBean() {
		return cameraBean;
	}

	public void setCameraBean(CameraBean cameraBean) {
		this.cameraBean = cameraBean;
	}

	public Flat getFlat() {
		return flat;
	}

	public void setFlat(Flat flat) {
		this.flat = flat;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public Person getLogPerson() {
		return logPerson;
	}

	public void setLogPerson(Person logPerson) {
		this.logPerson = logPerson;
	}

	public PersonType getPersonType() {
		return personType;
	}

	public void setPersonType(PersonType personType) {
		this.personType = personType;
	}

	public List<PersonType> getPersonTypes() {
		return personTypes;
	}

	public void setPersonTypes(List<PersonType> personTypes) {
		this.personTypes = personTypes;
	}

	public List<Gender> getGenders() {
		return genders;
	}

	public void setGenders(List<Gender> genders) {
		this.genders = genders;
	}

	public Phone getPhone() {
		return phone;
	}

	public void setPhone(Phone phone) {
		this.phone = phone;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public PhoneType getPhoneType() {
		return phoneType;
	}

	public void setPhoneType(PhoneType phoneType) {
		this.phoneType = phoneType;
	}

	public List<PhoneType> getPhoneTypes() {
		return phoneTypes;
	}

	public void setPhoneTypes(List<PhoneType> phoneTypes) {
		this.phoneTypes = phoneTypes;
	}

	public KinType getKinType() {
		return kinType;
	}

	public void setKinType(KinType kinType) {
		this.kinType = kinType;
	}

	public List<KinType> getKinTypes() {
		return kinTypes;
	}

	public void setKinTypes(List<KinType> kinTypes) {
		this.kinTypes = kinTypes;
	}

}

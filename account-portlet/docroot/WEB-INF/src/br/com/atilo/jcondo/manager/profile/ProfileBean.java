package br.com.atilo.jcondo.manager.profile;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.myfaces.commons.util.MessageUtils;

import br.com.abware.jcondo.core.Gender;
import br.com.abware.jcondo.core.PersonDetail;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.core.model.Phone;
import br.com.abware.jcondo.core.model.PhoneType;

import br.com.atilo.jcondo.core.service.PersonDetailServiceImpl;
import br.com.atilo.jcondo.core.service.PersonServiceImpl;
import br.com.atilo.jcondo.manager.CameraBean;
import br.com.atilo.jcondo.manager.ImageUploadBean;

@ViewScoped
@ManagedBean
public class ProfileBean {

	private static Logger LOGGER = Logger.getLogger(ProfileBean.class);
	
	private static final PersonDetailServiceImpl personDetailService = new PersonDetailServiceImpl();

	private PersonServiceImpl personService = new PersonServiceImpl();

	private ImageUploadBean imageUploadBean;

	private CameraBean cameraBean;

	private Person person;

	private PersonDetail personDetail;

	private List<Person> people;

	private List<Gender> genders;

	private List<Phone> phones;

	private List<PhoneType> phoneTypes;	

	private String phoneNumber;

	private PhoneType phoneType;

	private String password;

	private String newPassword;

	private String confirmPassword;

	@PostConstruct
	public void init() {
		try {
			person = personService.getPerson();
			personDetail = personDetailService.getPersonDetail(person);
			phones = personDetail.getPhones();
			phoneTypes = Arrays.asList(PhoneType.values());
			imageUploadBean = new ImageUploadBean(158, 240);
			cameraBean = new CameraBean(158, 240);
			genders = Arrays.asList(Gender.values());
		} catch (Exception e) {
			LOGGER.error("", e);
		}
	}
	
	public void onSave() {
		try {
			//person.setPicture(imageUploadBean.getImage());
			person = personService.update(person);
			personDetailService.update(personDetail);
			MessageUtils.addMessage(FacesMessage.SEVERITY_INFO, "profile.save.success", null);
		} catch (Exception e) {
			MessageUtils.addMessage(FacesMessage.SEVERITY_ERROR, "profile.save.failure", null);
			LOGGER.error("", e);
		}
	}

	public void onPasswordChange() throws Exception {
		personService.updatePassword(person, password, newPassword);
		MessageUtils.addMessage(FacesMessage.SEVERITY_INFO, "profile.pwd.change.success", null);
	}
	
	public void onPhoneAdd() {
		if (!validatePhoneNumber() || !validatePhoneType()) {
			return;
		}
		String pn = phoneNumber.replaceAll("[^0-9]*", "");
		String extension = StringUtils.left(pn, 2);
		String number = StringUtils.right(pn, pn.length() - 2);

		Phone phone = new Phone(extension, number, phoneType);
		phoneNumber = null;
		phoneType = null;

		if (phones.contains(phone)) {
			MessageUtils.addMessage(FacesMessage.SEVERITY_INFO, "profile.phone.already.added", null);
			return;
		}

		phones.add(0, phone);
	}

	public void onPhoneDelete(Phone phone) {
		phones.remove(phone);
	}
	
	public boolean validatePhoneNumber() {
		if (StringUtils.isEmpty(phoneNumber)) {
			MessageUtils.addMessage(FacesMessage.SEVERITY_WARN, "profile.empty.phone.number", null);
			return false;
		}

		if (phoneNumber.replaceAll("[^0-9]*", "").length() < 8) {
			MessageUtils.addMessage(FacesMessage.SEVERITY_WARN, "profile.invalid.phone.number", null);
			return false;
		}

		return true;
	} 
	
	public boolean validatePhoneType() {
		if (phoneType == null) {
			MessageUtils.addMessage(FacesMessage.SEVERITY_WARN, "profile.empty.phone.type", null);
			return false;
		}
		
		return true;
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

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public List<Person> getPeople() {
		return people;
	}

	public void setPeople(List<Person> people) {
		this.people = people;
	}

	public List<Gender> getGenders() {
		return genders;
	}

	public void setGenders(List<Gender> genders) {
		this.genders = genders;
	}

	public List<Phone> getPhones() {
		return phones;
	}

	public void setPhones(List<Phone> phones) {
		this.phones = phones;
	}

	public List<PhoneType> getPhoneTypes() {
		return phoneTypes;
	}

	public void setPhoneTypes(List<PhoneType> phoneTypes) {
		this.phoneTypes = phoneTypes;
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}



}

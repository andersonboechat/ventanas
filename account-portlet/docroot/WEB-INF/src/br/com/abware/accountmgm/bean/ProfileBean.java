package br.com.abware.accountmgm.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.myfaces.commons.util.MessageUtils;

import br.com.abware.accountmgm.util.KinTypePredicate;
import br.com.abware.accountmgm.util.RelativeTransformer;
import br.com.abware.jcondo.core.Gender;
import br.com.abware.jcondo.core.PersonDetail;
import br.com.abware.jcondo.core.PersonType;
import br.com.abware.jcondo.core.model.KinType;
import br.com.abware.jcondo.core.model.Kinship;
import br.com.abware.jcondo.core.model.Membership;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.core.model.Phone;
import br.com.abware.jcondo.core.model.PhoneType;

import br.com.atilo.jcondo.core.service.PersonDetailServiceImpl;

@ManagedBean
@ViewScoped
public class ProfileBean extends BaseBean {

	private static Logger LOGGER = Logger.getLogger(ProfileBean.class);
	
	private static final PersonDetailServiceImpl personDetailService = new PersonDetailServiceImpl();

	private ImageUploadBean imageUploadBean;

	private CameraBean cameraBean;

	private Person person;

	private PersonDetail personDetail;

	private Person father;

	private Person mother;

	private Person relative;

	private List<Person> people;

	private List<Kinship> kinships;

	private List<Gender> genders;

	private List<Phone> phones;

	private List<PhoneType> phoneTypes;	

	private String phoneNumber;

	private PhoneType phoneType;

	private String password;

	private String newPassword;

	private String confirmPassword;

	@SuppressWarnings("unchecked")
	@PostConstruct
	public void init() {
		try {
			HashSet<Person> ps = new HashSet<Person>();
			person = personService.getPerson();
			personDetail = personDetailService.getPersonDetail(person);
			phones = personDetail.getPhones();
			phoneTypes = Arrays.asList(PhoneType.values());
			
			for (Membership membership : person.getMemberships()) {
				if (membership.getType() == PersonType.OWNER || membership.getType() == PersonType.RENTER) {
					ps.addAll(personService.getPeople(membership.getDomain()));
				}
			}

			if (!CollectionUtils.isEmpty(ps)) {
				kinships = personDetail.getKinships();

				ps.remove(person);
				people = new ArrayList<Person>(ps);

				List<Person> relatives = (List<Person>) CollectionUtils.collect(kinships, new RelativeTransformer());

				people.removeAll(relatives);
				father = getParent(KinType.FATHER);
				mother = getParent(KinType.MOTHER);
				people.remove(father);
				people.remove(mother);
			}
			
			imageUploadBean = new ImageUploadBean(198, 300);
			cameraBean = new CameraBean(198, 300);
			genders = Arrays.asList(Gender.values());
		} catch (Exception e) {
			LOGGER.error("", e);
		}
	}
	
	public void onSave() {
		try {
			person.setPicture(imageUploadBean.getImage());
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

	public void onAddChild() {
		if (relative == null) {
			return;
		}

		kinships.add(new Kinship(person, relative, KinType.CHILD));
		people.remove(relative);
		relative = null;
	}	

	public void onRemoveChild(Person relative) {
		if (relative == null) {
			return;
		}

		kinships.remove(new Kinship(person, relative, KinType.CHILD));
		people.add(relative);
	}

	public void onAddGrandChild() {
		if (relative == null) {
			return;
		}

		kinships.add(new Kinship(person, relative, KinType.GRANDCHILD));
		people.remove(relative);
		relative = null;
	}	

	public void onRemoveGrandChild(Person relative) {
		if (relative == null) {
			return;
		}

		kinships.remove(new Kinship(person, relative, KinType.GRANDCHILD));
		people.add(relative);
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

	public Person getParent(KinType type) {
		if (type != KinType.FATHER && type != KinType.MOTHER) {
			return null;
		}
		Kinship kinship = (Kinship) CollectionUtils.find(kinships, new KinTypePredicate(type)); 
		return kinship != null ? kinship.getRelative() : null;
	}

	@SuppressWarnings("unchecked")
	public List<Person> getChildren() {
		List<Kinship> ks = (List<Kinship>) CollectionUtils.select(kinships, new KinTypePredicate(KinType.CHILD));
		return (List<Person>) CollectionUtils.collect(ks, new RelativeTransformer());
	}

	@SuppressWarnings("unchecked")
	public List<Person> getGrandChildren() {
		List<Kinship> ks = (List<Kinship>) CollectionUtils.select(kinships, new KinTypePredicate(KinType.GRANDCHILD));
		return (List<Person>) CollectionUtils.collect(ks, new RelativeTransformer());
	}

	public List<Person> getMothers() {
		List<Person> mothers = new ArrayList<Person>(people);
		CollectionUtils.addIgnoreNull(mothers, mother);
		return mothers;
	}

	public List<Person> getFathers() {
		List<Person> fathers = new ArrayList<Person>(people);
		CollectionUtils.addIgnoreNull(fathers, father);
		return fathers;
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

	public Person getFather() {
		return father;
	}

	public void setFather(Person father) {
		CollectionUtils.addIgnoreNull(people, this.father);
		people.remove(father);

		Kinship kinship = (Kinship) CollectionUtils.find(kinships, new KinTypePredicate(KinType.FATHER));

		if (kinship != null) {
			kinships.remove(kinship);
		}

		if (father != null) {
			kinships.add(new Kinship(person, father, KinType.FATHER));
		}

		this.father = father;
	}

	public Person getMother() {
		return mother;
	}

	public void setMother(Person mother) {
		CollectionUtils.addIgnoreNull(people, this.mother);
		people.remove(mother);

		Kinship kinship = (Kinship) CollectionUtils.find(kinships, new KinTypePredicate(KinType.MOTHER));

		if (kinship != null) {
			kinships.remove(kinship);
		}

		if (mother != null) {
			kinships.add(new Kinship(person, mother, KinType.MOTHER));
		}

		this.mother = mother;
	}

	public Person getRelative() {
		return relative;
	}

	public void setRelative(Person relative) {
		this.relative = relative;
	}

	public List<Person> getPeople() {
		return people;
	}

	public void setPeople(List<Person> people) {
		this.people = people;
	}

	public List<Kinship> getKinships() {
		return kinships;
	}

	public void setKinships(List<Kinship> kinships) {
		this.kinships = kinships;
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

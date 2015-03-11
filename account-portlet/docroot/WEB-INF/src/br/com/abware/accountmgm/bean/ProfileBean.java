package br.com.abware.accountmgm.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;

import br.com.abware.accountmgm.service.core.KinshipServiceImpl;
import br.com.abware.jcondo.core.Gender;
import br.com.abware.jcondo.core.PersonType;
import br.com.abware.jcondo.core.model.KinType;
import br.com.abware.jcondo.core.model.Kinship;
import br.com.abware.jcondo.core.model.Membership;
import br.com.abware.jcondo.core.model.Person;

@ManagedBean
@ViewScoped
public class ProfileBean extends BaseBean {

	private static Logger LOGGER = Logger.getLogger(ProfileBean.class);
	
	protected static final KinshipServiceImpl kinshipService = new KinshipServiceImpl();

	private ImageUploadBean imageUploadBean;

	private Person person;

	private List<Person> people;

	private List<Kinship> kinships;

	private List<Gender> genders;

	private List<KinType> types;

	@PostConstruct
	public void init() {
		try {
			HashSet<Person> ps = new HashSet<Person>();
			person = personService.getPerson();
			kinships = kinshipService.getKinships(person);

			for (Membership membership : person.getMemberships()) {
				if (membership.getType() == PersonType.OWNER || membership.getType() == PersonType.RENTER) {
					ps.addAll(personService.getPeople(membership.getDomain()));
				}
			}

			people = new ArrayList<Person>(ps);
			imageUploadBean = new ImageUploadBean(198, 300);
			genders = Arrays.asList(Gender.values());
			types = Arrays.asList(KinType.values());
		} catch (Exception e) {
			LOGGER.error("", e);
		}
	}
	
	public void onSave() {
		try {
			person.setPicture(imageUploadBean.getImage());
			person = personService.update(person);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ImageUploadBean getImageUploadBean() {
		return imageUploadBean;
	}

	public void setImageUploadBean(ImageUploadBean imageUploadBean) {
		this.imageUploadBean = imageUploadBean;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public List<Gender> getGenders() {
		return genders;
	}

	public void setGenders(List<Gender> genders) {
		this.genders = genders;
	}

}

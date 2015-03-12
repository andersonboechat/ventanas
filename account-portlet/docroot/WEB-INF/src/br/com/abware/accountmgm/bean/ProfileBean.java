package br.com.abware.accountmgm.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.log4j.Logger;

import br.com.abware.accountmgm.service.core.KinshipServiceImpl;
import br.com.abware.accountmgm.util.KinTypePredicate;
import br.com.abware.accountmgm.util.RelativeTransformer;
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

	private Person father;

	private Person mother;

	private Person relative;

	private List<Person> people;

	private List<Kinship> kinships;

	private List<Gender> genders;

	private List<KinType> types;

	@SuppressWarnings("unchecked")
	@PostConstruct
	public void init() {
		try {
			HashSet<Person> ps = new HashSet<Person>();
			person = personService.getPerson();

			for (Membership membership : person.getMemberships()) {
				if (membership.getType() == PersonType.OWNER || membership.getType() == PersonType.RENTER) {
					ps.addAll(personService.getPeople(membership.getDomain()));
				}
			}

			ps.remove(person);
			people = new ArrayList<Person>(ps);

			if (!CollectionUtils.isEmpty(people)) {
				kinships = kinshipService.getKinships(person);
				types = Arrays.asList(KinType.values());
			}

			List<Person> relatives = (List<Person>) CollectionUtils.collect(kinships, new Transformer() {
																				@Override
																				public Object transform(Object obj) {
																					if (obj != null && obj instanceof Kinship) {
																						return ((Kinship) obj).getRelative();
																					}
																					return null;
																				}
																			});

			people.removeAll(relatives);
			father = findRelative(KinType.FATHER);
			mother = findRelative(KinType.MOTHER);
			people.remove(father);
			people.remove(mother);

			imageUploadBean = new ImageUploadBean(198, 300);
			imageUploadBean.setImage(person.getPicture());
			genders = Arrays.asList(Gender.values());
		} catch (Exception e) {
			LOGGER.error("", e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void onSave() {
		try {
			person.setPicture(imageUploadBean.getImage());
			person = personService.update(person);

			List<Kinship> oldKinships = kinshipService.getKinships(person);

			for (Kinship kinship : (List<Kinship>) CollectionUtils.subtract(oldKinships, kinships)) {
				kinshipService.delete(kinship);
			}

			for (Kinship kinship : (List<Kinship>) CollectionUtils.subtract(kinships, oldKinships)) {
				kinshipService.register(kinship);
			}

		} catch (Exception e) {
			LOGGER.error("", e);
		}
	}

	public void onAddChild() {
		if (relative == null) {
			return;
		}

		kinships.add(new Kinship(person, relative, KinType.CHILD));
		people.remove(relative);
		relative = null;
	}	

	public void onRemoveChild() {
		if (relative == null) {
			return;
		}

		kinships.remove(new Kinship(person, relative, KinType.CHILD));
		people.add(relative);
		relative = null;
	}

	public Person findRelative(KinType type) {
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

	public Person getFather() {
		return father;
	}

	public void setFather(Person father) {
		if (this.father != null) {
			people.add(this.father);
		}
		people.remove(father);
		this.father = father;
	}

	public Person getMother() {
		return mother;
	}

	public void setMother(Person mother) {
		if (this.mother != null) {
			people.add(this.mother);
		}
		people.remove(mother);
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

	public List<KinType> getTypes() {
		return types;
	}

	public void setTypes(List<KinType> types) {
		this.types = types;
	}



}

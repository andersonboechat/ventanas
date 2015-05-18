package br.com.abware.accountmgm.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import br.com.abware.accountmgm.util.DomainPredicate;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Person;

@ManagedBean
@ViewScoped
public class SearchBean extends BaseBean {

	private static Logger LOGGER = Logger.getLogger(SearchBean.class);

	private Person person;

	private String personName;

	private String identity;
	
	private String license;
	
	private Flat flat;
	
	private List<Flat> flats;
	
	private List<Person> people;

	@PostConstruct
	public void init() {
		try {
			flats = flatService.getFlats();
		} catch (Exception e) {
			LOGGER.fatal("");
		}
	}

	public void onPersonSearch() throws Exception {
		if (!StringUtils.isEmpty(personName)) {
			people = personService.getPeople(person);
		}

		if (!StringUtils.isEmpty(identity)) {
			Person person;
			if (CollectionUtils.isEmpty(people)) {
				person = personService.getPerson(identity);
			} else {
				Person person = (Person) CollectionUtils.find(people, new DomainPredicate(flat));
			}

			if (person != null) {
				people = Arrays.asList(person);
			} else {
				
			}
		}

		if (flat != null) {
			if (CollectionUtils.isEmpty(people)) {
				people = personService.getPeople(flat);	
			} else {
				for (Person person : people) {
					if(!CollectionUtils.exists(person.getMemberships(), new DomainPredicate(flat))) {
						people.remove(person);
					}
				}
			}
		}
	}
	
	public void onVehicleSearch() {
		
	}
	
	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public String getPersonName() {
		return personName;
	}

	public void setPersonName(String personName) {
		this.personName = personName;
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public Flat getFlat() {
		return flat;
	}

	public void setFlat(Flat flat) {
		this.flat = flat;
	}

	public List<Flat> getFlats() {
		return flats;
	}

	public void setFlats(List<Flat> flats) {
		this.flats = flats;
	}
}

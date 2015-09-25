package br.com.atilo.jcondo.core.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;

import br.com.abware.jcondo.core.PersonDetail;
import br.com.abware.jcondo.core.model.Kinship;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.core.model.Phone;
import br.com.abware.jcondo.core.model.PhoneType;
import br.com.abware.jcondo.exception.BusinessException;

import br.com.atilo.jcondo.commons.collections.PhonePredicate;
import br.com.atilo.jcondo.core.persistence.manager.KinshipManagerImpl;
import br.com.atilo.jcondo.core.persistence.manager.PhoneManagerImpl;

public class PersonDetailServiceImpl {

	private KinshipManagerImpl kinshipManager = new KinshipManagerImpl();

	private PhoneManagerImpl phoneManager = new PhoneManagerImpl();

	@SuppressWarnings("unchecked")
	public void update(PersonDetail detail) throws Exception {
		List<Kinship> newKinships = new ArrayList<Kinship>(detail.getKinships());
		List<Kinship> oldKinships = kinshipManager.findByPerson(detail.getPerson());

		for (Kinship kinship : (List<Kinship>) CollectionUtils.subtract(oldKinships, detail.getKinships())) {
			kinshipManager.delete(kinship);
			newKinships.remove(kinship);
		}

		for (Kinship kinship : (List<Kinship>) CollectionUtils.subtract(detail.getKinships(), oldKinships)) {
			newKinships.add(kinshipManager.save(kinship));
		}

		detail.setKinships(newKinships);

		List<Phone> newPhones = new ArrayList<Phone>(detail.getPhones());
		List<Phone> oldPhones = phoneManager.findPhones(detail.getPerson());

		for (Phone phone : (List<Phone>) CollectionUtils.subtract(oldPhones, detail.getPhones())) {
			phoneManager.delete(phone.getId());
			newPhones.remove(phone);
		}

		for (Phone phone : (List<Phone>) CollectionUtils.subtract(detail.getPhones(), oldPhones)) {
			if (!NumberUtils.isDigits(phone.getExtension()) || !NumberUtils.isDigits(phone.getNumber())) {
				throw new BusinessException("pdt.phone.invalid");
			}
			newPhones.add(phoneManager.save(phone));
		}

		detail.setPhones(newPhones);
	}
	
	public PersonDetail getPersonDetail(Person person) throws Exception {
		PersonDetail detail = new PersonDetail();
		detail.setPerson(person);
		detail.setKinships(kinshipManager.findByPerson(person));
		detail.setPhones(phoneManager.findPhones(person));
		return detail;
	}
	
	public Phone getPhone(Person person) throws Exception {
		for (Phone phone : phoneManager.findPhones(person)) {
			if (phone.isPrimary()) {
				return phone;
			}
		}

		return null;
	}
	
	public List<Phone> getPhones(Person person, PhoneType type) throws Exception {
		List<Phone> phones = phoneManager.findPhones(person);

		if (type != null) {
			for (int i = phones.size() - 1; i >= 0; i--) {
				Phone phone = phones.get(i); 
				if (phone.getType() != type) {
					phones.remove(phone);
				}
			}
		}

		return phones;
	}

	public Kinship getKinship(Person person, Person relative) throws Exception {
		return kinshipManager.findByPersonAndRelative(person, relative);
	}

	public Kinship saveKinship(Kinship kinship) throws Exception {
		if (kinship.getPerson() == null || kinship.getRelative() == null || kinship.getType() == null) {
			throw new BusinessException("pdt.kinship.invalid");
		}

		return kinshipManager.save(kinship); 
	}

	public Phone savePhone(Person person, Phone phone) throws Exception {
		if (!NumberUtils.isDigits(phone.getExtension()) || !NumberUtils.isDigits(phone.getNumber())) {
			throw new BusinessException("pdt.phone.invalid");
		}
		
		Phone p = (Phone) CollectionUtils.find(phoneManager.findPhones(person), new PhonePredicate(phone));
		
		if (p != null) {
			if (p.getId() != phone.getId() && phone.getId() > 0) {
				phoneManager.delete(p.getId());
			}

			p.setExtension(phone.getExtension());
			p.setNumber(phone.getNumber());
			p.setType(phone.getType());
			p.setPrimary(phone.isPrimary());
		} else {
			p = phone;
		}

		return phoneManager.save(person, p);
	}
	
	public void removePhone(Phone phone) throws Exception {
		phoneManager.delete(phone.getId());
	}
}

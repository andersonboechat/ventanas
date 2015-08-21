package br.com.atilo.jcondo.core.service;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;

import br.com.abware.jcondo.core.PersonDetail;
import br.com.abware.jcondo.core.model.Kinship;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.core.model.Phone;
import br.com.abware.jcondo.core.model.PhoneType;
import br.com.abware.jcondo.exception.BusinessException;

import br.com.atilo.jcondo.core.persistence.manager.KinshipManagerImpl;
import br.com.atilo.jcondo.core.persistence.manager.PhoneManagerImpl;

public class PersonDetailServiceImpl {

	private KinshipManagerImpl kinshipManager = new KinshipManagerImpl();

	private PhoneManagerImpl phoneManager = new PhoneManagerImpl();

	@SuppressWarnings("unchecked")
	public void update(PersonDetail detail) throws Exception {
		List<Kinship> oldKinships = kinshipManager.findByPerson(detail.getPerson());

		for (Kinship kinship : (List<Kinship>) CollectionUtils.subtract(oldKinships, detail.getKinships())) {
			kinshipManager.delete(kinship);
		}

		for (Kinship kinship : (List<Kinship>) CollectionUtils.subtract(detail.getKinships(), oldKinships)) {
			kinshipManager.save(kinship);
		}

		List<Phone> oldPhones = phoneManager.findPhones(detail.getPerson());

		for (Phone phone : (List<Phone>) CollectionUtils.subtract(oldPhones, detail.getPhones())) {
			phoneManager.delete(phone);
		}

		for (Phone phone : (List<Phone>) CollectionUtils.subtract(detail.getPhones(), oldPhones)) {
			if (!NumberUtils.isDigits(phone.getExtension()) || !NumberUtils.isDigits(phone.getNumber())) {
				throw new BusinessException("pdt.phone.invalid");
			}
			phoneManager.save(phone);
		}
	}
	
	public PersonDetail getPersonDetail(Person person) throws Exception {
		PersonDetail detail = new PersonDetail();
		detail.setPerson(person);
		detail.setKinships(kinshipManager.findByPerson(person));
		detail.setPhones(phoneManager.findPhones(person));
		return detail;
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
}

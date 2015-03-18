package br.com.abware.accountmgm.service.core;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;

import br.com.abware.accountmgm.persistence.manager.KinshipManagerImpl;
import br.com.abware.accountmgm.persistence.manager.PhoneManagerImpl;
import br.com.abware.jcondo.core.PersonDetail;
import br.com.abware.jcondo.core.model.Kinship;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.core.model.Phone;

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
				throw new Exception("numero de telefone invalido");
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
}

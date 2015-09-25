package br.com.atilo.jcondo.core.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;

import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.core.model.Phone;
import br.com.abware.jcondo.core.model.PhoneType;
import br.com.abware.jcondo.exception.BusinessException;
import br.com.atilo.jcondo.commons.collections.PhonePredicate;
import br.com.atilo.jcondo.core.persistence.manager.PhoneManagerImpl;

public class PhoneServiceImpl {

	private PhoneManagerImpl phoneManager = new PhoneManagerImpl();

	public Phone getPhone(Person person) throws Exception {
		for (Phone phone : phoneManager.findPhones(person)) {
			if (phone.isPrimary()) {
				return phone;
			}
		}

		return null;
	}

	public Phone add(Person person, String extension, String number, PhoneType type, boolean isPrimary) throws Exception {
		if (!NumberUtils.isDigits(extension) || !NumberUtils.isDigits(number)) {
			throw new BusinessException("pdt.phone.invalid");
		}

		Phone phone = new Phone(extension, number, type);

		if (CollectionUtils.exists(phoneManager.findPhones(person), new PhonePredicate(phone))) {
			throw new BusinessException("pdt.phone.found");
		}

		phone.setPrimary(isPrimary);

		return phoneManager.save(person, phone);
	}

	public void remove(long id) throws Exception {
		phoneManager.delete(id);
	}

	public void update(long id, String extension, String number, PhoneType type, boolean isPrimary) throws Exception {
		if (!NumberUtils.isDigits(extension) || !NumberUtils.isDigits(number)) {
			throw new BusinessException("pdt.phone.invalid");
		}

		Phone phone = phoneManager.findById(id);

		if (phone == null) {
			throw new BusinessException("pdt.phone.not.found");
		}

		phone.setExtension(extension);
		phone.setNumber(number);
		phone.setType(type);
		phone.setPrimary(isPrimary);

		phoneManager.save(phone);		
	}

}

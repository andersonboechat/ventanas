package br.com.atilo.jcondo.core.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;

import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.core.model.Phone;
import br.com.abware.jcondo.core.model.PhoneType;
import br.com.abware.jcondo.exception.BusinessException;
import br.com.atilo.jcondo.commons.BeanUtils;
import br.com.atilo.jcondo.commons.collections.PhonePredicate;
import br.com.atilo.jcondo.core.persistence.manager.PhoneManagerImpl;

public class PhoneServiceImpl {

	private PhoneManagerImpl phoneManager = new PhoneManagerImpl();

	public Phone add(Person person, String extension, String number, PhoneType type, boolean isPrimary) throws Exception {
		if (!NumberUtils.isDigits(extension) || !NumberUtils.isDigits(number)) {
			throw new BusinessException("pdt.phone.invalid");
		}

		Phone phone = new Phone(extension, number, type);
		phone.setPrimary(isPrimary);

		Phone p = (Phone) CollectionUtils.find(phoneManager.findPhones(person), new PhonePredicate(phone));

		if (p != null) {
			BeanUtils.copyProperties(p, phone);
		} else {
			p = phone;
		}

		return phoneManager.save(person, p);
	}
}

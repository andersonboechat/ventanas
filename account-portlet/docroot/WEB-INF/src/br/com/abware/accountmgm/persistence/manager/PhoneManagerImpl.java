package br.com.abware.accountmgm.persistence.manager;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.liferay.portal.model.Contact;
import com.liferay.portal.model.ListType;
import com.liferay.portal.model.ListTypeConstants;
import com.liferay.portal.service.ListTypeServiceUtil;
import com.liferay.portal.service.PhoneLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;

import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.core.model.Phone;
import br.com.abware.jcondo.core.model.PhoneType;

public class PhoneManagerImpl extends LiferayManager<com.liferay.portal.model.Phone, Phone> {

	private PhoneType getPhoneType(String listTypeName) {
		if (listTypeName.equalsIgnoreCase("business")) {
			return PhoneType.WORK;
		}

		if (listTypeName.equalsIgnoreCase("tty")) {
			return PhoneType.HOME;
		}

		if (listTypeName.equalsIgnoreCase("mobile-phone")) {
			return PhoneType.MOBILE;
		}
		
		return null;
	}
	
	@Override
	protected Class<Phone> getModelClass() {
		return Phone.class;
	}

	@Override
	protected com.liferay.portal.model.Phone getEntity(Phone model) throws Exception {
		return null;
	}

	@Override
	protected Phone getModel(com.liferay.portal.model.Phone entity)	throws Exception {
		long number = Long.valueOf(entity.getExtension()) * 1000000000 + Long.valueOf(entity.getNumber()); 
		return new Phone(number, getPhoneType(entity.getType().getName()));
	}

	@Override
	public Phone save(Phone phone) throws Exception {
		long classPK = UserLocalServiceUtil.getUserById(helper.getUserId()).getContactId();
		String fullNumber = String.valueOf(phone.getNumber());
		String extension = StringUtils.left(fullNumber, 2);
		String number = StringUtils.right(fullNumber, 9);
		int typeId = 0;

		for (ListType listType : ListTypeServiceUtil.getListTypes(ListTypeConstants.CONTACT_PHONE)) {
			if (phone.getType() == getPhoneType(listType.getName())) {
				typeId = listType.getListTypeId();
				break;
			}
		}

		return getModel(PhoneLocalServiceUtil.addPhone(helper.getUserId(), Contact.class.getName(), classPK, number, extension, typeId, false));
	}

	@Override
	public void delete(Phone phone) throws Exception {
		PhoneLocalServiceUtil.deletePhone(phone.getId());
	}

	public List<Phone> findPhones(Person person) throws Exception {
		return getModels(PhoneLocalServiceUtil.getPhones(helper.getCompanyId(), Contact.class.getName(), UserLocalServiceUtil.getUserById(person.getUserId()).getContactId()));
	}

}

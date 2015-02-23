package br.com.abware.accountmgm.persistence.manager;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.exception.PersistenceException;

import com.liferay.portal.NoSuchOrganizationException;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.GroupConstants;
import com.liferay.portal.model.Organization;
import com.liferay.portal.model.OrganizationConstants;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.OrganizationLocalServiceUtil;
import com.liferay.portal.service.ResourceLocalServiceUtil;
import com.liferay.portlet.expando.model.ExpandoColumn;
import com.liferay.portlet.expando.model.ExpandoTableConstants;
import com.liferay.portlet.expando.model.ExpandoValue;
import com.liferay.portlet.expando.service.ExpandoColumnLocalServiceUtil;
import com.liferay.portlet.expando.service.ExpandoValueLocalServiceUtil;

public class FlatManagerImpl extends LiferayManager<Organization, Flat> {
	
	@Override
	protected Class<Flat> getModelClass() {
		return Flat.class;
	}

	@Override
	protected Organization getEntity(Flat flat) throws PersistenceException {
		try {
			Organization organization;

			try {
				organization = OrganizationLocalServiceUtil.getOrganization(flat.getId());
			} catch (NoSuchOrganizationException e) {
				organization = OrganizationLocalServiceUtil.createOrganization(flat.getId());
			}

			StringBuffer name = new StringBuffer();
			name.append(flat.getBlock()).append("/").append(StringUtils.leftPad(String.valueOf(flat.getNumber()), 4, "0"));

			organization.setName(name.toString());
			organization.setType(OrganizationConstants.TYPE_REGULAR_ORGANIZATION);

			return organization;
		} catch (Exception e) {
			throw new PersistenceException(e, "");
		}
	}
	
	@Override
	protected Flat getModel(Organization organization) throws Exception {
		String[] name = organization.getName().split("/");
		return new Flat(organization.getOrganizationId(), Integer.parseInt(name[0]), Integer.parseInt(name[1]));
	}

	public Flat save(Flat flat) throws PersistenceException {
//		Flat f = super.save(flat);

		if (flat.getRelatedId() == 0) {
			Organization org = OrganizationLocalServiceUtil.addOrganization(helper.getUserId(), 0, flat.getBlock() + "/" + flat.getNumber(), 
																			"flat", 
																			recursable, regionId, countryId, statusId, 
																			comments, site, serviceContext);
			ResourceLocalServiceUtil.addResources(helper.getCompanyId(), org.getGroupId(), helper.getUserId(), 
												  Flat.class.getName(), flat.getId(), false, true, false);
			f.setRelatedId(org.getGroupId());
			f = super.save(f);
		}

		return f;
	}

	public Flat findById(Object id) throws PersistenceException {
		try {
			return getModel(OrganizationLocalServiceUtil.getOrganization((Long) id));
		} catch (NoSuchOrganizationException e) {
			return null;
		} catch (Exception e) {
			throw new PersistenceException(e, "");
		}
	}

	public List<Flat> findAll() throws PersistenceException {
		try {
			return getModels(OrganizationLocalServiceUtil.getOrganizations(-1, -1));
		} catch (Exception e) {
			throw new PersistenceException(e, "");
		}
	}

	public List<Flat> findByPerson(Person person) throws PersistenceException {
		try {
			return getModels(OrganizationLocalServiceUtil.getUserOrganizations(person.getId()));
		} catch (Exception e) {
			throw new PersistenceException(e, "");
		}
	}	

	public Flat findByBlockAndNumber(long block, long number) throws PersistenceException {
		try {
			String name = block + "/" + StringUtils.leftPad(String.valueOf(number), 4, "0");
			return getModel(OrganizationLocalServiceUtil.getOrganization(helper.getCompanyId(), name));
		} catch (Exception e) {
			throw new PersistenceException(e, "");
		}
	}

	public List<Integer> findFlatBlocks() {
		try {
			ExpandoColumn column;
			column = ExpandoColumnLocalServiceUtil.getColumn(COMPANY, 
														     Organization.class.getName(), 
														     ExpandoTableConstants.DEFAULT_TABLE_NAME, 
														     BLOCK);
			String[] datas = column.getDefaultData().split(",");
			Integer[] blocks = Arrays.copyOf(datas, datas.length, Integer[].class);

			return Arrays.asList(blocks);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public List<Integer> findFlatNumbers() {
		try {
			ExpandoColumn column;
			column = ExpandoColumnLocalServiceUtil.getColumn(COMPANY, 
														     Organization.class.getName(), 
														     ExpandoTableConstants.DEFAULT_TABLE_NAME, 
														     NUMBER);
			String[] datas = column.getDefaultData().split(",");
			Integer[] numbers = Arrays.copyOf(datas, datas.length, Integer[].class);

			return Arrays.asList(numbers);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

}

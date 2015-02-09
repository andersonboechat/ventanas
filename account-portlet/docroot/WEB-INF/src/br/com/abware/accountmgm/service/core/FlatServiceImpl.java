package br.com.abware.accountmgm.service.core;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import br.com.abware.accountmgm.persistence.manager.NewFlatManagerImpl;
import br.com.abware.accountmgm.persistence.manager.SecurityManagerImpl;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.exception.BusinessException;
import br.com.abware.jcondo.exception.PersistenceException;

public class FlatServiceImpl {

	private static NewFlatManagerImpl flatManager = new NewFlatManagerImpl();

	private static SecurityManagerImpl securityManager = new SecurityManagerImpl();

	public Flat getFlat(long flatId) throws Exception {
		return flatManager.findById(flatId);
	}

	public List<Flat> getFlats() throws BusinessException {
		return null; //flatManager.findAll();
	}

	public List<Flat> getFlats(Person person) throws Exception {
		List<Flat> flats = null;
		try {
			flats = flatManager.findByPerson(person);

			if (CollectionUtils.isEmpty(flats)) {
				flats = flatManager.findAll();
			}
			
//			while (flats.iterator().hasNext()) {
//				try {
//					Flat flat = flats.iterator().next();
//					if (!securityManager.hasPermission(flat, Permission.VIEW)) {
//						flats.remove(flat);
//					}
//				} catch (ApplicationException e) {
//					// Do nothing
//				}
//			}
		} catch (PersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return flats;
	}

	public Flat register(Flat flat) throws Exception {
		Flat f = flatManager.findByNumberAndBlock(flat.getNumber(), flat.getBlock());

		if (f != null) {
			throw new Exception("Este apartamento j� existe");
		}

		return flatManager.save(flat);
	}
	
	public void delete(Flat flat) {
		
	}
}

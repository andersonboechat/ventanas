package br.com.abware.accountmgm.service.core;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import br.com.abware.accountmgm.persistence.manager.FlatManagerImpl;
import br.com.abware.accountmgm.persistence.manager.NewFlatManagerImpl;
import br.com.abware.accountmgm.persistence.manager.SecurityManagerImpl;
import br.com.abware.jcondo.core.Permission;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.core.service.FlatService;
import br.com.abware.jcondo.exception.ApplicationException;
import br.com.abware.jcondo.exception.BusinessException;
import br.com.abware.jcondo.exception.PersistenceException;

public class FlatServiceImpl {

	private static final NewFlatManagerImpl flatManager = new NewFlatManagerImpl();

	private static SecurityManagerImpl securityManager = new SecurityManagerImpl();

	private static List<Integer> blocks;

	private static List<Integer> numbers;

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

	public List<Integer> getBlocks() throws Exception {
		if (blocks == null) {
//			blocks = flatManager.findFlatBlocks();
		}
		return blocks;
	}

	public List<Integer> getNumbers() throws Exception {
		if (numbers == null) {
//			numbers = flatManager.findFlatNumbers();
		}
		return numbers;
	}

}

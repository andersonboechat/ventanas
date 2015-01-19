package br.com.abware.accountmgm.service.core;

import java.util.List;

import br.com.abware.accountmgm.persistence.manager.FlatManagerImpl;
import br.com.abware.accountmgm.persistence.manager.SecurityManagerImpl;
import br.com.abware.jcondo.core.Permission;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.core.service.FlatService;
import br.com.abware.jcondo.exception.ApplicationException;
import br.com.abware.jcondo.exception.BusinessException;
import br.com.abware.jcondo.exception.PersistenceException;

public class FlatServiceImpl implements FlatService {

	private static final FlatManagerImpl flatManager = new FlatManagerImpl();

	private static SecurityManagerImpl securityManager = new SecurityManagerImpl();

	private static List<Integer> blocks;

	private static List<Integer> numbers;

	@Override
	public List<Flat> getFlats() throws BusinessException {
		return null; //flatManager.findAll();
	}

	@Override
	public List<Flat> getFlats(Person person) throws BusinessException {
		List<Flat> flats = null;
		try {
			flats = flatManager.findByPerson(person);

			while (flats.iterator().hasNext()) {
				try {
					Flat flat = flats.iterator().next();
					if (!securityManager.hasPermission(flat, Permission.VIEW)) {
						flats.remove(flat);
					}
				} catch (ApplicationException e) {
					// Do nothing
				}
			}
		} catch (PersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return flats;
	}

	@Override
	public List<Integer> getBlocks() {
		if (blocks == null) {
			blocks = flatManager.findFlatBlocks();
		}
		return blocks;
	}

	@Override
	public List<Integer> getNumbers() {
		if (numbers == null) {
			numbers = flatManager.findFlatNumbers();
		}
		return numbers;
	}

}

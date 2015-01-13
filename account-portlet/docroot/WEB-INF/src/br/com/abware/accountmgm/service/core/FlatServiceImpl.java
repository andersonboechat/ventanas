package br.com.abware.accountmgm.service.core;

import java.util.List;

import br.com.abware.accountmgm.persistence.manager.FlatManagerImpl;
import br.com.abware.jcondo.core.BaseRole;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.core.service.FlatService;
import br.com.abware.jcondo.exception.PersistenceException;

public class FlatServiceImpl implements FlatService {

	private static final FlatManagerImpl flatManager = new FlatManagerImpl(); 
	
	private static List<Integer> blocks;

	private static List<Integer> numbers;

	@Override
	public List<Flat> getFlats() throws PersistenceException {
		return flatManager.findAll();
	}

	@Override
	public List<Flat> getFlats(Person person) throws PersistenceException {
		return null;
	}

	@Override
	public List<BaseRole> getRoles() {
		return null;
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

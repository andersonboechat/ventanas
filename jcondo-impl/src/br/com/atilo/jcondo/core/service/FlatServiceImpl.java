package br.com.atilo.jcondo.core.service;

import java.util.ArrayList;
import java.util.List;

import br.com.abware.jcondo.core.Permission;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.exception.PersistenceException;

import br.com.atilo.jcondo.core.persistence.manager.FlatManagerImpl;
import br.com.atilo.jcondo.core.persistence.manager.SecurityManagerImpl;

public class FlatServiceImpl {

	private static FlatManagerImpl flatManager = new FlatManagerImpl();

	private static SecurityManagerImpl securityManager = new SecurityManagerImpl();
	
	public Flat getFlat(long flatId) throws Exception {
		return flatManager.findById(flatId);
	}

	public List<Flat> getFlats() throws Exception {
		return flatManager.findAll();
	}

	public List<Flat> getPersonFlats(Person person) throws Exception {
		List<Flat> flats = new ArrayList<Flat>();
		try {
			for (Flat flat : flatManager.findByPerson(person)) {
				if (securityManager.hasPermission(flat, Permission.VIEW)) {
					flats.add(flat);
				}
			}
		} catch (PersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return flats;
		
	}
	
	public List<Flat> getFlats(Person person) throws Exception {
		List<Flat> flats = new ArrayList<Flat>();
		try {
			for (Flat flat : flatManager.findAll()) {
				if (securityManager.hasPermission(flat, Permission.VIEW)) {
					flats.add(flat);
				}
			}
		} catch (PersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return flats;
	}

	public Flat register(Flat flat) throws Exception {
		Flat f = flatManager.findByNumberAndBlock(flat.getNumber(), flat.getBlock());

		if (f != null) {
			throw new Exception("Este apartamento já existe");
		}

		return flatManager.save(flat);
	}
	
	public Flat update(Flat flat) throws Exception {
//		Flat f = flatManager.findByNumberAndBlock(flat.getNumber(), flat.getBlock());
//
//		if (f == null) {
//			throw new Exception("Este apartamento nao encontrado");
//		}
//
//		return flatManager.save(flat);
		return flat;
	}

	public void delete(Flat flat) {
		
	}
}

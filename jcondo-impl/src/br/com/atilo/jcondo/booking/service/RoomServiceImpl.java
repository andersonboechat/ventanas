package br.com.atilo.jcondo.booking.service;

import java.util.List;

import br.com.abware.jcondo.booking.model.Room;
import br.com.atilo.jcondo.booking.persistence.manager.RoomManagerImpl;

public class RoomServiceImpl {

	public static final long CINEMA = 3;
	
	public RoomManagerImpl roomManager = new RoomManagerImpl();
	
	public List<Room> getRooms(boolean onlyAvailable) throws Exception {
		return onlyAvailable ? roomManager.findAvailableRooms() : roomManager.findAll();
	}
}

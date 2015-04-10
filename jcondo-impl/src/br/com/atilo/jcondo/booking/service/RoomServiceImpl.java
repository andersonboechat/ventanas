package br.com.atilo.jcondo.booking.service;

import java.util.ArrayList;
import java.util.List;

import br.com.abware.jcondo.booking.model.Room;
import br.com.abware.jcondo.core.Permission;
import br.com.atilo.jcondo.booking.persistence.manager.RoomManagerImpl;
import br.com.atilo.jcondo.core.persistence.manager.SecurityManagerImpl;

public class RoomServiceImpl {

	public static final long CINEMA = 3;

	private RoomManagerImpl roomManager = new RoomManagerImpl();

	private SecurityManagerImpl securityManager = new SecurityManagerImpl();

	public List<Room> getRooms(boolean onlyAvailable) throws Exception {
		List<Room> rooms = new ArrayList<Room>();

		for (Room room : onlyAvailable ? roomManager.findAvailableRooms() : roomManager.findAll()) {
			if (securityManager.hasPermission(room, Permission.VIEW)) {
				rooms.add(room);	
			}
		}

		return rooms;
	}
}

package br.com.atilo.jcondo.booking.bean;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;

import br.com.abware.jcondo.booking.model.Room;

@ManagedBean
@ViewScoped
public class RoomBean extends BaseBean {

	private static final Logger LOGGER = Logger.getLogger(RoomBean.class);

	public static final List<Room> ROOMS = initRooms();

	private Room room;

	private static List<Room> initRooms() {
		List<Room> rooms;

		try {
			rooms = roomService.getRooms(true);
		} catch (Exception e) {
			rooms = new ArrayList<Room>();
			LOGGER.fatal("Rooms load failure", e);
		}

		return rooms;
	}

	public void init() {
		
	}

	public void onCreate() {
		
	}

	public void onSave() {
		
	}

	public void onDelete() {
		
	}
	
	public void onDisable() {
		
	}

	public void onEnable() {
		
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}
	
}

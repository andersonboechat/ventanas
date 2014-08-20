package br.com.abware.agenda;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import br.com.abware.agenda.persistence.entity.Room;
import br.com.abware.agenda.persistence.manager.RoomManager;
import br.com.abware.agenda.util.BeanUtils;

public class RoomModel {

	private static final Logger LOGGER = Logger.getLogger(RoomModel.class);
	
	private final RoomManager manager = new RoomManager();
	
	private int id;

	private String name;

	private String agreement;

	private boolean available;
	
	private int capacity;
	
	private double price;

	private String detail;
	
	private List<Image> images;
	
	public RoomModel() {
	}

	public RoomModel(int roomId) {
		this.id = roomId;
	}

	public List<RoomModel> getRooms() {
		return getRooms(true);
	}

	public List<RoomModel> getRooms(boolean available) {
		String owner = String.valueOf("RoomModel.getRooms");
		ArrayList<RoomModel> rms = new ArrayList<RoomModel>();
		List<Room> rooms;
		RoomModel rm;

		try {
			manager.openManager(owner);
			if (available) {
				rooms = manager.findAvailableRooms();
			} else {
				rooms = manager.findAll();
			}

			for (Room room : rooms) {
				rm = new RoomModel();
				BeanUtils.copyProperties(rm, room);
				rm.setImages(manager.findRoomImages(room.getImageFolderId()));
				rms.add(rm);
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} finally {
			manager.closeManager(owner);
		}

		return rms;
	}

	public RoomModel getRoom(long roomId) throws Exception {
		String owner = String.valueOf("RoomModel.getRoom");

		try {
			manager.openManager(owner);
			Room room = manager.findById(roomId);
			RoomModel roomModel = new RoomModel();
			BeanUtils.copyProperties(roomModel, room);
			roomModel.setImages(manager.findRoomImages(room.getImageFolderId()));
			return roomModel;
		} catch (Exception e) {
			throw new Exception();
		}  finally {
			manager.closeManager(owner);
		}

	}
	
	@Override
	public boolean equals(Object obj) {
		if (super.equals(obj)) {
			return true;
		}

		if (obj != null && obj instanceof RoomModel) {
			RoomModel rm = (RoomModel) obj;

			if (rm.getId() == this.id) {
				return true;
			}
		}
		
		return false;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAgreement() {
		return agreement;
	}

	public void setAgreement(String agreement) {
		this.agreement = agreement;
	}

	public boolean getAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public List<Image> getImages() {
		return images;
	}

	public void setImages(List<Image> images) {
		this.images = images;
	}


}

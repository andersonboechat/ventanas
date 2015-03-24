package br.com.atilo.jcondo.booking.persistence.manager;

import java.util.List;

import javax.persistence.Query;

import br.com.abware.jcondo.booking.model.Room;
import br.com.atilo.jcondo.booking.persistence.entity.RoomEntity;
import br.com.atilo.jcondo.core.persistence.manager.JCondoManager;

public class RoomManagerImpl extends JCondoManager<RoomEntity, Room> {

	@Override
	protected Class<Room> getModelClass() {
		return Room.class;
	}

	@Override
	protected Class<RoomEntity> getEntityClass() {
		return RoomEntity.class;
	}

	@SuppressWarnings("unchecked")
	public List<Room> findAvailableRooms() {
		String queryString = "FROM RoomEntity WHERE available = 1";
		Query query = em.createQuery(queryString);
		return query.getResultList();
	}	

}

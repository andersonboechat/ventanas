package br.com.atilo.jcondo.booking.persistence.manager;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
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
	public List<Room> findAvailableRooms() throws Exception {
		String key = generateKey();
		try {
			openManager(key);
			String queryString = "FROM RoomEntity WHERE available = 1";
			Query query = em.createQuery(queryString);
			return getModels(query.getResultList());
		} catch (NoResultException e) {
			return new ArrayList<Room>();
		} finally {
			closeManager(key);
		}
	}	

}

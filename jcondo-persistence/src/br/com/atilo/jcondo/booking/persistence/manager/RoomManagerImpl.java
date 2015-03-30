package br.com.atilo.jcondo.booking.persistence.manager;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import br.com.abware.jcondo.booking.model.Room;
import br.com.abware.jcondo.core.model.ArchiveType;
import br.com.atilo.jcondo.booking.persistence.entity.RoomEntity;
import br.com.atilo.jcondo.core.persistence.manager.ArchiveManagerImpl;
import br.com.atilo.jcondo.core.persistence.manager.JCondoManager;

public class RoomManagerImpl extends JCondoManager<RoomEntity, Room> {

	private ArchiveManagerImpl archiveManager = new ArchiveManagerImpl();
	
	@Override
	protected Class<Room> getModelClass() {
		return Room.class;
	}

	@Override
	protected Class<RoomEntity> getEntityClass() {
		return RoomEntity.class;
	}

	@Override
	protected RoomEntity getEntity(Room room) throws Exception {
		RoomEntity re = super.getEntity(room);
		re.setAgreementId(room.getAgreement().getId());
		return re;
	}
	
	@Override
	protected Room getModel(RoomEntity re) throws Exception {
		Room room = super.getModel(re);
		room.setAgreement(archiveManager.findById(re.getAgreementId()));
		room.setImages(archiveManager.findByFolderIdAndType(re.getFolderId(), ArchiveType.IMAGE));
		return room;
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

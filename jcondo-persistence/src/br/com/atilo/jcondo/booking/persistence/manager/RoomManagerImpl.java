package br.com.atilo.jcondo.booking.persistence.manager;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.collections.CollectionUtils;

import br.com.abware.jcondo.booking.model.Room;
import br.com.abware.jcondo.core.model.Archive;
import br.com.abware.jcondo.core.model.Document;
import br.com.atilo.jcondo.booking.persistence.entity.RoomEntity;
import br.com.atilo.jcondo.core.persistence.manager.ArchiveManagerImpl;
import br.com.atilo.jcondo.core.persistence.manager.DocumentManagerImpl;
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
	protected Room getModel(RoomEntity entity) throws Exception {
		Room room = super.getModel(entity);
		List<Archive> docs = archiveManager.findByFolderIdAndType(entity.getFolderId(), "agreement-term");
		Document agreement = (Document) (!CollectionUtils.isEmpty(docs) ? docs.get(0) : null);
		room.setAgreement(agreement);

		docs = archiveManager.findByFolderIdAndType(entity.getFolderId(), "image");
		room.setImages(images);
		
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

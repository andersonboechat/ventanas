package br.com.abware.agenda.persistence.manager;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portlet.documentlibrary.service.DLAppLocalServiceUtil;

import br.com.abware.agenda.Image;
import br.com.abware.agenda.persistence.entity.Room;

public class RoomManager extends BaseManager<Room>{

	private static final long REPOSITORY_ID = 10179;
	
	private static final String base_path = "http://www.ventanasresidencial.com.br/documents/";

	@SuppressWarnings("unchecked")
	public List<Room> findAvailableRooms() {
		String queryString = "FROM Room WHERE available = 1";
		Query query = em.createQuery(queryString);
		return query.getResultList();
	}

	public List<Image> findRoomImages(long imageFolderId) {
		List<Image> images = new ArrayList<Image>();
		Image image;
		
		try {
			List<FileEntry> fileEntries = DLAppLocalServiceUtil.getFileEntries(REPOSITORY_ID, imageFolderId);
			
			for (FileEntry fe : fileEntries) {
				image = new Image(fe.getTitle(), fe.getDescription(), getImagePath(fe));
				images.add(image);
			}
		} catch (PortalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return images;
	}
	
	@Override
	protected Class<Room> getEntityClass() {
		return Room.class;
	}

	private String getImagePath(FileEntry fe) throws PortalException, SystemException {
		return base_path + fe.getRepositoryId() + "/" + fe.getFolderId() + "/" + fe.getTitle();
	}
}

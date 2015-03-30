package br.com.atilo.jcondo.core.persistence.manager;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import br.com.abware.jcondo.core.model.Image;

import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portlet.documentlibrary.NoSuchFolderException;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.model.DLFolder;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLFolderLocalServiceUtil;

public class ImageManagerImpl extends LiferayManager<DLFileEntry, Image> {
	
	private String getPath(long groupId, long folderId, String fileName) {
		return new StringBuffer(helper.getPortalURL()).append("/images/")
				.append(groupId).append("/").append(folderId).append("/").append(fileName).toString(); 
	}

	@Override
	protected Class<Image> getModelClass() {
		return Image.class;
	}

	protected DLFileEntry getEntity(Image image) throws Exception {
		return DLFileEntryLocalServiceUtil.getFileEntry(image.getId());
	}
	
	@Override
	protected Image getModel(DLFileEntry entity) throws Exception {
		Image image = new Image(entity.getFileEntryId(), 
								getPath(entity.getGroupId(), entity.getFolderId(), entity.getTitle()), 
								entity.getTitle(), entity.getDescription());
		return image;
	} 

	public List<Image> findByFolderId(long folderId) throws Exception {
		try {
			ArrayList<Image> images = new ArrayList<Image>();
			DLFolder folder = DLFolderLocalServiceUtil.getFolder(folderId);

			for (DLFileEntry fileEntry : DLFileEntryLocalServiceUtil.getFileEntries(folder.getGroupId(), folder.getFolderId(), -1, -1, null)) {
				if (fileEntry.getMimeType().matches("image/(jpeg|gif|png)")) {
					images.add(getModel(fileEntry));
				}
			}

			return images;
		} catch (NoSuchFolderException e) {
			return new ArrayList<Image>();
		}
	}
	
	public Image saveAt(Image image, long folderId) throws Exception {
		if (StringUtils.isEmpty(image.getPath())) {
			throw new Exception("caminho do arquivo nao definido");
		}

		File file = new File(new URL(image.getPath()).getPath());
		DLFolder folder = DLFolderLocalServiceUtil.getFolder(folderId);
		DLFileEntry fileEntry = getEntity(image);

		if (fileEntry == null) {
			fileEntry = DLFileEntryLocalServiceUtil.addFileEntry(helper.getUserId(), folder.getGroupId(), folder.getRepositoryId(), 
																 folder.getFolderId(), file.getName(), MimeTypesUtil.getContentType(file), 
																 folder.getName(), StringUtils.EMPTY, null, 0, 
																 null, file, null, FileUtils.sizeOf(file), new ServiceContext());
		} else {
			fileEntry = DLFileEntryLocalServiceUtil.updateFileEntry(helper.getUserId(), fileEntry.getFileEntryId(), file.getName(), 
																	MimeTypesUtil.getContentType(file),	folder.getName(), StringUtils.EMPTY, 
																	StringUtils.EMPTY, true, 0, null, file, null, 
																	FileUtils.sizeOf(file), new ServiceContext());
		}

		return getModel(fileEntry);
	}

	@Override
	public Image save(Image model) throws Exception {
		throw new UnsupportedOperationException("use saveAt method");
	}

	@Override
	public void delete(Image model) {
		// TODO Auto-generated method stub
		
	}

}

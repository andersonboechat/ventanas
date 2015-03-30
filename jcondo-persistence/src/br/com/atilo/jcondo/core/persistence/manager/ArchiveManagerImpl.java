package br.com.atilo.jcondo.core.persistence.manager;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import br.com.abware.jcondo.core.model.Archive;
import br.com.abware.jcondo.core.model.ArchiveType;
import br.com.abware.jcondo.core.model.Document;
import br.com.abware.jcondo.core.model.Image;

import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portlet.documentlibrary.NoSuchFileEntryTypeException;
import com.liferay.portlet.documentlibrary.NoSuchFolderException;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.model.DLFileEntryType;
import com.liferay.portlet.documentlibrary.model.DLFolder;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLFileEntryTypeLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLFolderLocalServiceUtil;

public class ArchiveManagerImpl extends LiferayManager<DLFileEntry, Archive> {
	
	private String getPath(long groupId, long folderId, String fileName) {
		return new StringBuffer(helper.getPortalURL()).append("/documents/")
				.append(groupId).append("/").append(folderId).append("/").append(fileName).toString(); 
	}
	
	private ArchiveType getArchiveType(String type) {
		
	}

	@Override
	protected Class<Archive> getModelClass() {
		return Archive.class;
	}

	protected DLFileEntry getEntity(Archive archive) throws Exception {
		return DLFileEntryLocalServiceUtil.getFileEntry(archive.getId());
	}
	
	@Override
	protected Archive getModel(DLFileEntry entity) throws Exception {
		Archive archive = new Archive(entity.getFileEntryId(), 
									  entity.getFolderId(), 
									  entity.getf, 
									  getPath(entity.getGroupId(), entity.getFolderId(), entity.getTitle()), 
									  entity.getName(), entity.getDescription());

		return archive;
	} 

	public List<Archive> findByFolderId(long folderId) throws Exception {
		try {
			DLFolder folder = DLFolderLocalServiceUtil.getFolder(folderId);
			return getModels(DLFileEntryLocalServiceUtil.getFileEntries(folder.getGroupId(), folder.getFolderId(), -1, -1, null));
		} catch (NoSuchFolderException e) {
			return new ArrayList<Archive>();
		}
	}

	public List<Archive> findByFolderIdAndType(long folderId, ArchiveType type) throws Exception {
		ArrayList<Archive> archives = new ArrayList<Archive>();
		DLFolder folder = DLFolderLocalServiceUtil.getFolder(folderId);
		
		if (type == ArchiveType.IMAGE) {
			for (DLFileEntry fileEntry : DLFileEntryLocalServiceUtil.getFileEntries(folder.getGroupId(), folder.getFolderId(), -1, -1, null)) {
				if (fileEntry.getMimeType().startsWith("image")) {
					archives.add(getModel(fileEntry));
				}
			}			
		} else {
			DLFileEntryType fileType = DLFileEntryTypeLocalServiceUtil.getFileEntryType(folder.getGroupId(), type.name());
			for (DLFileEntry fileEntry : DLFileEntryLocalServiceUtil.getFileEntries(folder.getGroupId(), folder.getFolderId(), -1, -1, null)) {
				if (fileEntry.getFileEntryId() == fileType.getFileEntryTypeId()) {
					archives.add(getModel(fileEntry));
				}
			}
		}

		return archives;
	}

	@Override
	public Archive save(Archive archive) throws Exception {
		if (StringUtils.isEmpty(archive.getPath())) {
			throw new Exception("caminho do arquivo nao definido");
		}

		File file = new File(new URL(archive.getPath()).getPath());
		DLFolder folder = DLFolderLocalServiceUtil.getFolder(archive.getFolderId());
		DLFileEntry fileEntry = getEntity(archive);
		long fileEntryTypeId;

		try {
			fileEntryTypeId = DLFileEntryTypeLocalServiceUtil.getFileEntryType(folder.getGroupId(), "").getFileEntryTypeId();
		} catch (NoSuchFileEntryTypeException e) {
			fileEntryTypeId = 0;
		}

		if (fileEntry == null) {
			fileEntry = DLFileEntryLocalServiceUtil.addFileEntry(helper.getUserId(), folder.getGroupId(), folder.getRepositoryId(), 
																 folder.getFolderId(), archive.getName(), MimeTypesUtil.getContentType(file), 
																 folder.getName(), StringUtils.EMPTY, null, fileEntryTypeId, 
																 null, file, null, FileUtils.sizeOf(file), new ServiceContext());
		} else {
			fileEntry = DLFileEntryLocalServiceUtil.updateFileEntry(helper.getUserId(), fileEntry.getFileEntryId(), archive.getName(), 
																	MimeTypesUtil.getContentType(file),	folder.getName(), StringUtils.EMPTY, 
																	StringUtils.EMPTY, true, fileEntryTypeId, null, file, null, 
																	FileUtils.sizeOf(file), new ServiceContext());
		}

		return getModel(fileEntry);
	}

	@Override
	public void delete(Archive model) {
		// TODO Auto-generated method stub
		
	}

}

package br.com.atilo.jcondo.core.persistence.manager;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import br.com.abware.jcondo.core.model.Archive;
import br.com.abware.jcondo.core.model.ArchiveType;

import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portlet.documentlibrary.NoSuchFileEntryException;
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
	
	private ArchiveType getArchiveType(DLFileEntry entity) throws Exception {
		if (entity.getMimeType().startsWith("image")) {
			return ArchiveType.IMAGE;
		} else {
			DLFileEntryType type = DLFileEntryTypeLocalServiceUtil.getDLFileEntryType(entity.getFileEntryTypeId());
			for (ArchiveType at : ArchiveType.values()) {
				if (type.getName().equals(at.name())) {
					return at;
				}
			}
		}
		return null;
	}

	@Override
	protected Class<Archive> getModelClass() {
		return Archive.class;
	}

	protected DLFileEntry getEntity(Archive archive) throws Exception {
		return DLFileEntryLocalServiceUtil.getFileEntry(archive.getId());
	}
	
	@Override
	protected Archive getModel(DLFileEntry file) throws Exception {
		return new Archive(file.getFileEntryId(), 
						   file.getFolderId(), 
						   getArchiveType(file), 
						   getPath(file.getGroupId(), file.getFolderId(), file.getTitle()), 
						   file.getName(), file.getDescription());
	} 

	public Archive findById(long id) throws Exception {
		try {
			return getModel(DLFileEntryLocalServiceUtil.getDLFileEntry(id));	
		} catch (NoSuchFileEntryException e) {
			return null;
		}
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
		try {
			DLFolder folder = DLFolderLocalServiceUtil.getFolder(folderId);

			for (DLFileEntry fileEntry : DLFileEntryLocalServiceUtil.getFileEntries(folder.getGroupId(), folder.getFolderId(), -1, -1, null)) {
				if (getArchiveType(fileEntry) == type) {
					archives.add(getModel(fileEntry));
				}
			}
		} catch (NoSuchFolderException e) {
			System.out.println("folder not found");
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
			fileEntryTypeId = DLFileEntryTypeLocalServiceUtil.getFileEntryType(folder.getGroupId(), 
																			   archive.getType().name()).getFileEntryTypeId();
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

package br.com.atilo.jcondo.core.persistence.manager;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import br.com.abware.jcondo.core.model.Document;
import br.com.abware.jcondo.core.model.Domain;

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

public class DocumentManagerImpl extends LiferayManager<DLFileEntry, Document> {
	
	private FlatManagerImpl flatManager = new FlatManagerImpl();

	private String getPath(long groupId, long folderId, String fileName) {
		return new StringBuffer(helper.getPortalURL()).append("/documents/")
				.append(groupId).append("/").append(folderId).append("/").append(fileName).toString(); 
	}

	@Override
	protected Class<Document> getModelClass() {
		return Document.class;
	}

	protected DLFileEntry getEntity(Document model) throws Exception {
		return DLFileEntryLocalServiceUtil.getFileEntry(model.getId());
	}
	
	@Override
	protected Document getModel(DLFileEntry entity) throws Exception {
		Document document = new Document(entity.getFileEntryId(), 
							entity.getTitle(), 
							getPath(entity.getGroupId(), entity.getFolderId(), entity.getTitle()));

		document.setDomain(flatManager.findByFolder(entity.getFolderId()));

		return document;
	} 

	public List<Document> findByDomain(Domain domain) throws Exception {
		try {
			DLFolder folder = DLFolderLocalServiceUtil.getFolder(domain.getFolderId());
			return getModels(DLFileEntryLocalServiceUtil.getFileEntries(folder.getGroupId(), folder.getFolderId(), -1, -1, null));
		} catch (NoSuchFolderException e) {
			return new ArrayList<Document>();
		}
	}

	public Document findByDomainAndType(Domain domain, String type) throws Exception {
		DLFolder folder = DLFolderLocalServiceUtil.getFolder(domain.getFolderId());
		DLFileEntryType fileType = DLFileEntryTypeLocalServiceUtil.getFileEntryType(folder.getFolderId(), type);
		
		for (DLFileEntry fileEntry : DLFileEntryLocalServiceUtil.getFileEntries(folder.getGroupId(), folder.getFolderId(), -1, -1, null)) {
			if (fileEntry.getFileEntryId() == fileType.getFileEntryTypeId()) {
				return getModel(fileEntry); 
			}
		}

		return null;
	}

	@Override
	public Document save(Document model) throws Exception {
		if (StringUtils.isEmpty(model.getPath())) {
			throw new Exception("caminho do arquivo nao definido");
		}

		File file = new File(new URL(model.getPath()).getPath());
		DLFolder folder = DLFolderLocalServiceUtil.getFolder(model.getDomain().getFolderId());
		DLFileEntry fileEntry = getEntity(model);
		long fileEntryTypeId;

		try {
			fileEntryTypeId = DLFileEntryTypeLocalServiceUtil.getFileEntryType(folder.getGroupId(), "").getFileEntryTypeId();
		} catch (NoSuchFileEntryTypeException e) {
			fileEntryTypeId = 0;
		}

		if (fileEntry == null) {
			fileEntry = DLFileEntryLocalServiceUtil.addFileEntry(helper.getUserId(), folder.getGroupId(), folder.getRepositoryId(), 
																 folder.getFolderId(), model.getName(), MimeTypesUtil.getContentType(file), 
																 folder.getName(), StringUtils.EMPTY, null, fileEntryTypeId, 
																 null, file, null, FileUtils.sizeOf(file), new ServiceContext());
		} else {
			fileEntry = DLFileEntryLocalServiceUtil.updateFileEntry(helper.getUserId(), fileEntry.getFileEntryId(), model.getName(), 
																	MimeTypesUtil.getContentType(file),	folder.getName(), StringUtils.EMPTY, 
																	StringUtils.EMPTY, true, fileEntryTypeId, null, file, null, 
																	FileUtils.sizeOf(file), new ServiceContext());
		}

		return getModel(fileEntry);
	}

	@Override
	public void delete(Document model) {
		// TODO Auto-generated method stub
		
	}

}

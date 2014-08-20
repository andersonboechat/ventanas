package br.com.abware.agenda.bean;

import java.util.List;

import org.junit.Test;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.service.DLAppLocalServiceUtil;

public class GalleryBeanTest {

	@Test
	public void myTest() throws Exception {
		List<FileEntry> fileEntries = DLAppLocalServiceUtil.getFileEntries(10179, 11010);

		for (FileEntry fe : fileEntries) {
			System.out.println(getImagePath(fe));
		}		
	}

	private String getImagePath(FileEntry fe) throws PortalException, SystemException {
		DLFileEntry dlfe = (DLFileEntry) fe.getModel(); 

		return fe.getRepositoryId() + "/" + fe.getFolderId() + "/" + dlfe.getName();
	}

}

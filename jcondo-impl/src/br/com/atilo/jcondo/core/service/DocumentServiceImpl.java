package br.com.atilo.jcondo.core.service;

import java.util.List;

import br.com.abware.jcondo.core.model.Document;
import br.com.abware.jcondo.core.model.Domain;
import br.com.atilo.jcondo.core.persistence.manager.DocumentManagerImpl;

public class DocumentServiceImpl {
	
	private DocumentManagerImpl documentManager = new DocumentManagerImpl();
	
	public List<Document> getDocuments(Domain domain) throws Exception {
		return documentManager.findByFolderId(domain.getFolderId());
	}

	public Document register(Document document) throws Exception {
		return documentManager.save(document);
	}

	public Document update(Document document) {
		return null;
	}

	public void delete(Document document) {
		
	}

}

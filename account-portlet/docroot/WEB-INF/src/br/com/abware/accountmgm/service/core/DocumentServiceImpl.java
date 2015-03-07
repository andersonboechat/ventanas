package br.com.abware.accountmgm.service.core;

import java.util.List;

import br.com.abware.accountmgm.persistence.manager.DocumentManagerImpl;
import br.com.abware.jcondo.core.model.Document;
import br.com.abware.jcondo.core.model.Domain;

public class DocumentServiceImpl {
	
	private DocumentManagerImpl documentManager = new DocumentManagerImpl();
	
	public List<Document> getDocuments(Domain domain) throws Exception {
		return documentManager.findByDomain(domain);
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

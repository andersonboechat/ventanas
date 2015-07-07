package br.com.atilo.jcondo.manager.flat;

import java.util.HashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;

import br.com.abware.accountmgm.bean.model.ModelDataModel;
import br.com.abware.jcondo.core.model.Document;
import br.com.abware.jcondo.core.model.Domain;
import br.com.abware.jcondo.core.model.Flat;
import br.com.atilo.jcondo.core.service.DocumentServiceImpl;

@ViewScoped
@ManagedBean(name="documentView")
public class DocumentBean {
	
	private static Logger LOGGER = Logger.getLogger(Document.class);
	
	private final DocumentServiceImpl documentService = new DocumentServiceImpl();	

	private ModelDataModel<Document> model;

	private HashMap<String, Object> filters;

	private Document document;

	private Document[] selectedDocuments;

	private Flat flat;

	public void init(Flat flat) {
		try {
			this.flat = flat;
			model = new ModelDataModel<Document>(documentService.getDocuments(flat));
			filters = new HashMap<String, Object>();
			document = new Document();
		} catch (Exception e) {
			LOGGER.error("", e);
		}
	}

	public void onDomainSearch(Domain domain) throws Exception {
		filters.put("domain",  domain != null ? domain : null);
		model.filter(filters);
	}

	public void onCreate() {
		document = new Document();
	}

	public void onSave() throws Exception {
		Document doc;

		if (document.getId() == 0) {
			doc = documentService.register(document);
			model.addModel(doc);
		} else {
			doc = documentService.update(document);
			model.setModel(doc);
		}

	}

	public void onEdit() throws Exception {
		BeanUtils.copyProperties(document, model.getRowData());
	}

	public void onDelete() {
		documentService.delete(document);
	}

	public void onDocumentsDelete() {
		for (Document doc : selectedDocuments) {
			documentService.delete(doc);
		}
	}

	public ModelDataModel<Document> getModel() {
		return model;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public Flat getFlat() {
		return flat;
	}

	public void setFlat(Flat flat) {
		this.flat = flat;
	}

}

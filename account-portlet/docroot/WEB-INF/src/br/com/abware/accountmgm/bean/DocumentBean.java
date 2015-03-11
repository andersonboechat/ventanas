package br.com.abware.accountmgm.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;

import br.com.abware.accountmgm.bean.model.ModelDataModel;
import br.com.abware.accountmgm.util.BeanUtils;
import br.com.abware.jcondo.core.model.Document;
import br.com.abware.jcondo.core.model.Domain;

@ManagedBean
@ViewScoped
public class DocumentBean extends BaseBean {
	
	private static Logger LOGGER = Logger.getLogger(Document.class);

	private ModelDataModel<Document> model;

	private HashMap<String, Object> filters;

	private Document document;

	private Document[] selectedDocuments;

	public void init(List<? extends Domain> domains) {
		try {
			List<Document> documents = new ArrayList<Document>();

			for (Domain domain: domains) {
				documents.addAll(documentService.getDocuments(domain));
			}

			model = new ModelDataModel<Document>(documents);
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
		document.setDomain(new Domain());
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

}

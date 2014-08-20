package br.com.abware.complaintbook.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import br.com.abware.complaintbook.OccurrenceModel;
import br.com.abware.complaintbook.UserHelper;

@ManagedBean
@ViewScoped
public class BookAdmin {

	private static Logger logger = Logger.getLogger(BookBean.class);
	
	private static ResourceBundle rb = ResourceBundle.getBundle("Language", new Locale("pt", "BR"));
	
	private String text;
	
	private OccurrenceModel occurrence;
	
	private List<OccurrenceModel> occurrences;

	public BookAdmin() {
		logger.trace("Method in");

		try {
			this.occurrence = new OccurrenceModel();
			this.occurrences = OccurrenceModel.getOccurrences();
			logger.debug("Amount of occurrence found: " + occurrences.size());
			if (CollectionUtils.isEmpty(occurrences)) {
				OccurrenceModel occurrence = new OccurrenceModel();
				occurrence.setId(-1);
				occurrences.add(0, occurrence);
			}
		} catch (Exception e) {
			logger.fatal(e.getMessage(), e);
			this.occurrences = new ArrayList<OccurrenceModel>();
		}		

		logger.trace("Method out");
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public OccurrenceModel getOccurrence() {
		return occurrence;
	}

	public void setOccurrence(OccurrenceModel occurrence) {
		this.occurrence = occurrence;
	}

	public List<OccurrenceModel> getOccurrences() {
		return occurrences;
	}

	public void setOccurrences(List<OccurrenceModel> occurrences) {
		this.occurrences = occurrences;
	}

}

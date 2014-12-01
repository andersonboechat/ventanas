package br.com.abware.complaintbook;

import java.util.List;

import javax.faces.model.ListDataModel;
import org.primefaces.model.SelectableDataModel;

public class OccurrenceDataModel extends ListDataModel<OccurrenceModel> implements SelectableDataModel<OccurrenceModel> {

	public OccurrenceDataModel(List<OccurrenceModel> data) {
		super(data);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public OccurrenceModel getRowData(String rowKey) {
        List<OccurrenceModel> occurrences = (List<OccurrenceModel>) getWrappedData();  
        
        for(OccurrenceModel occurrence : occurrences) {  
            if (Integer.parseInt(rowKey) == occurrence.getId()) {
				return occurrence;
            }
        }  
          
        return null;
	}

	@Override
	public Object getRowKey(OccurrenceModel occurrence) {
		return occurrence.getId();
	}

}

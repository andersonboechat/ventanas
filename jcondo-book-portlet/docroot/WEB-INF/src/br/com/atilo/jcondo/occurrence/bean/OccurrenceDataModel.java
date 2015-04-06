package br.com.atilo.jcondo.occurrence.bean;

import java.util.List;

import javax.faces.model.ListDataModel;
import org.primefaces.model.SelectableDataModel;

import br.com.abware.jcondo.crm.model.Occurrence;

public class OccurrenceDataModel extends ListDataModel<Occurrence> implements SelectableDataModel<Occurrence> {

	public OccurrenceDataModel(List<Occurrence> data) {
		super(data);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Occurrence getRowData(String rowKey) {
        List<Occurrence> occurrences = (List<Occurrence>) getWrappedData();  
        
        for(Occurrence occurrence : occurrences) {  
            if (Integer.parseInt(rowKey) == occurrence.getId()) {
				return occurrence;
            }
        }  
          
        return null;
	}

	@Override
	public Object getRowKey(Occurrence occurrence) {
		return occurrence.getId();
	}

	@SuppressWarnings("unchecked")
	public void add(Occurrence occurrence) {
		((List<Occurrence>) getWrappedData()).add(0, occurrence);
	}

}

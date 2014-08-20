package br.com.abware.accountmgm;

import java.util.List;

import javax.faces.model.ListDataModel;

import org.primefaces.model.SelectableDataModel;

public class FlatUserDataModel extends ListDataModel<FlatUser> implements SelectableDataModel<FlatUser> {

	public FlatUserDataModel() {
	}

	public FlatUserDataModel(List<FlatUser> data) {
		super(data);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public FlatUser getRowData(String rowKey) {
        //In a real app, a more efficient way like a query by rowKey should be implemented to deal with huge data  
        List<FlatUser> users = (List<FlatUser>) getWrappedData();  
          
        for(FlatUser user : users) {  
            if(Long.parseLong(rowKey) == user.getUserId()) {
				return user;
            }
        }  
          
        return null;
	}

	@Override
	public Object getRowKey(FlatUser user) {
		return user.getUserId();
	}

	@SuppressWarnings("unchecked")
	public void add(FlatUser user) {
		List<FlatUser> users = (List<FlatUser>) getWrappedData();
		users.add(user);
	}
}

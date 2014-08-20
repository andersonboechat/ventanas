package br.com.abware.accountmgm;

import java.util.List;

import javax.faces.model.ListDataModel;

import org.primefaces.model.SelectableDataModel;

public class UserDataModel extends ListDataModel<UserAdapter> implements SelectableDataModel<UserAdapter> {

	public UserDataModel(List<UserAdapter> data) {
		super(data);
	}	

	@Override
	@SuppressWarnings("unchecked")
	public UserAdapter getRowData(String rowKey) {
        //In a real app, a more efficient way like a query by rowKey should be implemented to deal with huge data  
        List<UserAdapter> users = (List<UserAdapter>) getWrappedData();  
          
        for(UserAdapter user : users) {  
            if(Long.parseLong(rowKey) == user.getUserId()) {
				return user;
            }
        }  
          
        return null;
	}

	@Override
	public Object getRowKey(UserAdapter user) {
		return user.getUserId();
	}

	@SuppressWarnings("unchecked")
	public void add(UserAdapter user) {
		List<UserAdapter> users = (List<UserAdapter>) getWrappedData();
		users.add(user);
	}
}

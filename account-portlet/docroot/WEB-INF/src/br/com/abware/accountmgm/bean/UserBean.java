package br.com.abware.accountmgm.bean;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;

import br.com.abware.accountmgm.bean.model.FlatUserDataModel;
import br.com.abware.accountmgm.model.Flat;
import br.com.abware.accountmgm.model.FlatUser;

@ManagedBean
@ViewScoped
public class UserBean {

	private static Logger LOGGER = Logger.getLogger(UserBean.class);
	
	private static final FlatUserDataModel model = null;
	
	private List<String> groups;
	
	private String group;
	
	private Object supplier;
	
	private Flat flat;
	
	private FlatUser user;
	
	private FlatUser[] selectedUsers;
	
	@PostConstruct
	public void init() {
		
	}
	
	public void onUserCreate() {
		
	}
	
	public void onUserDelete() {
		
	}
	
	public void onUserEdit() {
		
	}
	
	public void onGroupSelect() {
		
	}
	
	public void onSupplierSelect() {
		
	}
	
	public void onFlatSelect() {
		
	}

	public boolean hasPermission() {
		return false;
	}

	public List<String> getUserTypes() {
		return null;
	}
	
	public List<String> getGroups() {
		return groups;
	}

	public void setGroups(List<String> groups) {
		this.groups = groups;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public Object getSupplier() {
		return supplier;
	}

	public void setSupplier(Object supplier) {
		this.supplier = supplier;
	}

	public Flat getFlat() {
		return flat;
	}

	public void setFlat(Flat flat) {
		this.flat = flat;
	}

	public FlatUser getUser() {
		return user;
	}

	public void setUser(FlatUser user) {
		this.user = user;
	}

	public FlatUser[] getSelectedUsers() {
		return selectedUsers;
	}

	public void setSelectedUsers(FlatUser[] selectedUsers) {
		this.selectedUsers = selectedUsers;
	}

}

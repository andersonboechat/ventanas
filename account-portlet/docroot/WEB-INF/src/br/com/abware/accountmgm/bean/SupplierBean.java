package br.com.abware.accountmgm.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;

import org.apache.log4j.Logger;

import br.com.abware.accountmgm.bean.model.ModelDataModel;
import br.com.abware.accountmgm.util.BeanUtils;
import br.com.abware.jcondo.core.SupplierStatus;
import br.com.abware.jcondo.core.model.Domain;
import br.com.abware.jcondo.core.model.Supplier;


@ManagedBean
@ViewScoped
public class SupplierBean extends BaseBean {

	private static Logger LOGGER = Logger.getLogger(SupplierBean.class);

	@ManagedProperty(value="#{personBean}")
	private PersonBean personBean;

	@ManagedProperty(value="#{imageUploadBean}")
	private ImageUploadBean imageUploadBean;	

	private ModelDataModel<Supplier> model = null;
	
	private HashMap<String, Object> filters;
	
	private Supplier supplier;
	
	private List<Supplier> suppliers;
	
	private Map<Domain, List<SupplierStatus>> statuses;
	
	private String name;

	private SupplierStatus status;

	private Supplier[] selectedSuppliers;
	
	public void init(List<? extends Domain> domains) {
		try {
			suppliers = new ArrayList<Supplier>();
			statuses = new HashMap<Domain, List<SupplierStatus>>();

			for (Domain domain: domains) {
				suppliers.addAll(supplierService.getSuppliers(domain));
				statuses.put(domain, supplierService.getStatuses(domain));
			}

			model = new ModelDataModel<Supplier>(new ArrayList<Supplier>(suppliers));
			filters = new HashMap<String, Object>();
			imageUploadBean.setWidth(198);
			imageUploadBean.setHeight(300);
			personBean.init(suppliers);
		} catch (Exception e) {
			LOGGER.error("", e);
		}
	}

	public void onSupplierSave(AjaxBehaviorEvent event) throws Exception {
		try {
			Supplier sup;
			supplier.setPicture(imageUploadBean.getImage());

			if (supplier.getId() == 0) {
				sup = supplierService.register(supplier);
				model.addModel(sup);
			} else {
				sup = supplierService.update(supplier);
				model.setModel(sup);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	public void onSupplierDelete() throws Exception {
		supplierService.delete(supplier);
	}
	
	public void onSuppliersDelete() throws Exception {
		for (Supplier supplier : selectedSuppliers) {
			supplierService.delete(supplier);
		}
	}

	public void onSupplierEdit(AjaxBehaviorEvent event) throws Exception {
		try {
			BeanUtils.copyProperties(supplier, model.getRowData());
			imageUploadBean.setImage(supplier.getPicture());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onDomainSearch(Domain domain) throws Exception {
		filters.put("id", domain != null ? domain.getId() : null);
		model.filter(filters);
	}

	public void onNameSearch() throws Exception {
		filters.put("name", name);
		model.filter(filters);
	}

	public void onStatusSearch() throws Exception {
		filters.put("status", status);
		model.filter(filters);
	}

	public ModelDataModel<Supplier> getModel() {
		return model;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public List<Supplier> getSuppliers() {
		return suppliers;
	}

	public void setSuppliers(List<Supplier> suppliers) {
		this.suppliers = suppliers;
	}

}

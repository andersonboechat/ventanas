package br.com.abware.accountmgm.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.apache.myfaces.commons.util.MessageUtils;

import br.com.abware.accountmgm.bean.model.ModelDataModel;
import br.com.abware.jcondo.core.SupplierStatus;
import br.com.abware.jcondo.core.model.Domain;
import br.com.abware.jcondo.core.model.Supplier;
import br.com.abware.jcondo.exception.BusinessException;


@ManagedBean
@ViewScoped
public class SupplierBean extends BaseBean {

	private static Logger LOGGER = Logger.getLogger(SupplierBean.class);
	
	private ModelDataModel<Supplier> model = null;
	
	private HashMap<String, Object> filters;
	
	private Supplier supplier;
	
	private List<Supplier> suppliers;
	
	private Map<Domain, List<SupplierStatus>> statuses;
	
	private String name;

	private SupplierStatus status;

	private Supplier[] selectedSuppliers;
	
	private Domain domain;
	
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
			supplier = new Supplier();
		} catch (Exception e) {
			LOGGER.fatal("Failure on supplier initialization", e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_FATAL, "general.failure", null);
		}
	}

	public void onSupplierCreate() throws Exception {
		supplier = new Supplier();
	}
	
	public void onSupplierSave() throws Exception {
		try {
			Supplier sup;
			supplier.setParent(domain);

			if (supplier.getId() == 0) {
				supplier.setStatus(SupplierStatus.ENABLED);
				sup = supplierService.register(supplier);
				model.addModel(sup);
			} else {
				sup = supplierService.update(supplier);
				model.setModel(sup);
			}

			this.setChanged();
			this.notifyObservers(sup);

			MessageUtils.addMessage(FacesMessage.SEVERITY_INFO, "supplier.create.success", null);
		} catch (BusinessException e) {
			MessageUtils.addMessage(FacesMessage.SEVERITY_WARN, e.getMessage(), e.getArgs());
		} catch (Exception e) {
			LOGGER.error("Failure on supplier saving", e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_ERROR, "general.failure", null);
		}
	}

	public void onSupplierDelete() throws Exception {
		try {
			supplierService.delete(supplier);
		} catch (BusinessException e) {
			MessageUtils.addMessage(FacesMessage.SEVERITY_WARN, e.getMessage(), e.getArgs());
		} catch (Exception e) {
			LOGGER.error("Failure on supplier saving", e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_ERROR, "general.failure", null);
		}
	}
	
	public void onSuppliersDelete() throws Exception {
		try {
			for (Supplier supplier : selectedSuppliers) {
				supplierService.delete(supplier);
			}
		} catch (BusinessException e) {
			MessageUtils.addMessage(FacesMessage.SEVERITY_WARN, e.getMessage(), e.getArgs());
		} catch (Exception e) {
			LOGGER.error("Failure on supplier saving", e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_ERROR, "general.failure", null);
		}
	}

	public void onSupplierEdit() throws Exception {
		try {
			BeanUtils.copyProperties(supplier, model.getRowData());
			//domain = (Domain) BeanUtils.cloneBean(supplier.getParent());
		} catch (Exception e) {
			LOGGER.error("Unexpected failure on supplier editing", e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_ERROR, "general.failure", null);
		}
	}

	public void onDomainSearch(Domain domain) throws Exception {
		filters.put("parent.id", domain != null ? domain.getId() : null);
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

	public Map<Domain, List<SupplierStatus>> getStatuses() {
		return statuses;
	}

	public void setStatuses(Map<Domain, List<SupplierStatus>> statuses) {
		this.statuses = statuses;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public SupplierStatus getStatus() {
		return status;
	}

	public void setStatus(SupplierStatus status) {
		this.status = status;
	}

	public Supplier[] getSelectedSuppliers() {
		return selectedSuppliers;
	}

	public void setSelectedSuppliers(Supplier[] selectedSuppliers) {
		this.selectedSuppliers = selectedSuppliers;
	}

	public Domain getDomain() {
		return domain;
	}

	public void setDomain(Domain domain) {
		this.domain = domain;
	}

}

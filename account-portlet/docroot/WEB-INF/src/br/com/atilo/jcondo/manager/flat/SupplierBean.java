package br.com.atilo.jcondo.manager.flat;

import java.util.HashMap;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.apache.myfaces.commons.util.MessageUtils;
import org.primefaces.context.RequestContext;

import br.com.abware.accountmgm.bean.model.ModelDataModel;
import br.com.abware.jcondo.core.SupplierStatus;
import br.com.abware.jcondo.core.model.Domain;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Supplier;
import br.com.abware.jcondo.exception.BusinessException;
import br.com.atilo.jcondo.core.service.SupplierServiceImpl;


@ViewScoped
@ManagedBean(name="supplierView")
public class SupplierBean {

	private static Logger LOGGER = Logger.getLogger(SupplierBean.class);

	private SupplierServiceImpl supplierService = new SupplierServiceImpl();

	private ModelDataModel<Supplier> model = null;

	private HashMap<String, Object> filters;

	private Supplier supplier;

	private List<Supplier> suppliers;

	private List<SupplierStatus> statuses;

	private String name;

	private SupplierStatus status;

	private Supplier[] selectedSuppliers;

	private Flat flat;

	public void init(Flat flat) {
		try {
			this.flat = flat;
			statuses = supplierService.getStatuses(flat);
			model = new ModelDataModel<Supplier>(supplierService.getSuppliers(flat));
			filters = new HashMap<String, Object>();
			supplier = new Supplier();
			supplier.setParent(flat);
		} catch (Exception e) {
			LOGGER.fatal("Failure on supplier initialization", e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_FATAL, "general.failure", null);
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
		}
	}

	public void onSupplierCreate() throws Exception {
		supplier = new Supplier();
		supplier.setParent(flat);
	}
	
	public void onSupplierSave() throws Exception {
		try {
			Supplier sup;

			if (supplier.getId() == 0) {
				supplier.setStatus(SupplierStatus.ENABLED);
				sup = supplierService.register(supplier);
				model.addModel(sup);
			} else {
				sup = supplierService.update(supplier);
				model.setModel(sup);
			}

//			this.setChanged();
//			this.notifyObservers(sup);

			MessageUtils.addMessage(FacesMessage.SEVERITY_INFO, "supplier.create.success", null);
		} catch (BusinessException e) {
			MessageUtils.addMessage(FacesMessage.SEVERITY_WARN, e.getMessage(), e.getArgs());
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
		} catch (Exception e) {
			LOGGER.error("Failure on supplier saving", e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_ERROR, "general.failure", null);
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
		}
	}

	public void onSupplierDelete() throws Exception {
		try {
			supplierService.delete(supplier);
		} catch (BusinessException e) {
			MessageUtils.addMessage(FacesMessage.SEVERITY_WARN, e.getMessage(), e.getArgs());
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
		} catch (Exception e) {
			LOGGER.error("Failure on supplier saving", e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_ERROR, "general.failure", null);
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
		}
	}
	
	public void onSuppliersDelete() throws Exception {
		try {
			for (Supplier supplier : selectedSuppliers) {
				supplierService.delete(supplier);
			}
		} catch (BusinessException e) {
			MessageUtils.addMessage(FacesMessage.SEVERITY_WARN, e.getMessage(), e.getArgs());
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
		} catch (Exception e) {
			LOGGER.error("Failure on supplier saving", e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_ERROR, "general.failure", null);
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
		}
	}

	public void onSupplierEdit() throws Exception {
		try {
			BeanUtils.copyProperties(supplier, model.getRowData());
			//domain = (Domain) BeanUtils.cloneBean(supplier.getParent());
		} catch (Exception e) {
			LOGGER.error("Unexpected failure on supplier editing", e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_ERROR, "general.failure", null);
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
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

	public List<SupplierStatus> getStatuses() {
		return statuses;
	}

	public void setStatuses(List<SupplierStatus> statuses) {
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

	public Flat getFlat() {
		return flat;
	}

	public void setFlat(Flat flat) {
		this.flat = flat;
	}

}

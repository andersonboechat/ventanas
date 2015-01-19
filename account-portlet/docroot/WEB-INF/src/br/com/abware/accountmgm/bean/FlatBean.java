package br.com.abware.accountmgm.bean;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;

import br.com.abware.accountmgm.bean.model.ModelDataModel;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Person;

@ManagedBean
@ViewScoped
public class FlatBean extends BaseBean {

	private static Logger LOGGER = Logger.getLogger(FlatBean.class);

	private ModelDataModel<Flat> model;

	private Person person;

	private Flat flat;

	private List<Integer> flatBlocks;

	private List<Integer> flatNumbers;

	@PostConstruct
	public void init() {
		try {
			person = personService.getPerson();
			model = new ModelDataModel<Flat>(flatService.getFlats(person));
			flat = model.getRowData();
			flatBlocks = flatService.getBlocks();
			flatNumbers = flatService.getNumbers();
		} catch (Exception e) {
			LOGGER.error("", e);
		}
	}

	public void onFlatSave() {
		
	}
	
	public void onFlatDelete() {
		
	}
	
	public void onFlatEdit() {
		
	}

	public boolean hasPermission() {
		return false;
	}

	public ModelDataModel<Flat> getModel() {
		return model;
	}

	public Flat getFlat() {
		return flat;
	}

	public void setFlat(Flat flat) {
		this.flat = flat;
	}

	public List<Integer> getFlatBlocks() {
		return flatBlocks;
	}

	public void setFlatBlocks(List<Integer> flatBlocks) {
		this.flatBlocks = flatBlocks;
	}

	public List<Integer> getFlatNumbers() {
		return flatNumbers;
	}

	public void setFlatNumbers(List<Integer> flatNumbers) {
		this.flatNumbers = flatNumbers;
	}	
}

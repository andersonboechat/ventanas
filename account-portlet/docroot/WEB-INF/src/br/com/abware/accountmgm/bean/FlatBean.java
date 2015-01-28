package br.com.abware.accountmgm.bean;

import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

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
	
	private HashMap<String, Object> filters;	

	private Person person;

	private Flat flat;

	private TreeSet<Long> blocks;

	private TreeSet<Long> numbers;

	@PostConstruct
	public void init() {
		try {
			person = personService.getPerson();
			List<Flat> flats = flatService.getFlats(person);
			model = new ModelDataModel<Flat>(flats);

			blocks = new TreeSet<Long>();
			numbers = new TreeSet<Long>();
			for (Flat flat : flats) {
				blocks.add(flat.getBlock());
				numbers.add(flat.getNumber());
			}

			filters = new HashMap<String, Object>();
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

	public TreeSet<Long> getBlocks() {
		return blocks;
	}

	public void setBlocks(TreeSet<Long> blocks) {
		this.blocks = blocks;
	}

	public TreeSet<Long> getNumbers() {
		return numbers;
	}

	public void setNumbers(TreeSet<Long> numbers) {
		this.numbers = numbers;
	}	
}

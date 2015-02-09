package br.com.abware.accountmgm.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;

import org.apache.log4j.Logger;

import br.com.abware.accountmgm.bean.model.ModelDataModel;
import br.com.abware.accountmgm.util.BeanUtils;
import br.com.abware.jcondo.core.model.Flat;

@ManagedBean
@ViewScoped
public class FlatBean extends BaseBean {

	private static Logger LOGGER = Logger.getLogger(FlatBean.class);

	private ModelDataModel<Flat> model;

	private HashMap<String, Object> filters;	

	private Set<Long> blocks;

	private Long block;

	private Set<Long> numbers;

	private Long number;

	private List<Flat> flats;
	
	private Flat flat;
	
	private Flat[] selectedFlats;

	@PostConstruct
	public void init() {
		try {
			flats = flatService.getFlats(personService.getPerson());
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

	public void onBlockSelect(AjaxBehaviorEvent event) throws Exception {
		filters.put("block", block);
		model.filter(filters);
	}

	public void onNumberSelect(AjaxBehaviorEvent event) throws Exception {
		filters.put("number", number);
		model.filter(filters);
	}

	public void onFlatCreate() {
		flat = new Flat();
	}
	
	public void onFlatSave() {
		try {
			boolean isNew = flat.getId() == 0;
			flat = flatService.register(flat);

			if (isNew) {
				model.addModel(flat);
			} else {
				model.setModel(flat);
			}		
		} catch (Exception e) {
			
		}
	}
	
	public void onFlatDelete() {
		flatService.delete(flat);
	}

	public void onFlatsDelete() {
		for (Flat flat : selectedFlats) {
			flatService.delete(flat);
		}
	}

	public void onFlatEdit() {
		try {
			flat = new Flat();
			BeanUtils.copyProperties(flat, model.getRowData());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ModelDataModel<Flat> getModel() {
		return model;
	}

	public Set<Long> getBlocks() {
		return blocks;
	}

	public void setBlocks(Set<Long> blocks) {
		this.blocks = blocks;
	}

	public Long getBlock() {
		return block;
	}

	public void setBlock(Long block) {
		this.block = block;
	}

	public Set<Long> getNumbers() {
		return numbers;
	}

	public void setNumbers(Set<Long> numbers) {
		this.numbers = numbers;
	}

	public Long getNumber() {
		return number;
	}

	public void setNumber(Long number) {
		this.number = number;
	}

	public List<Flat> getFlats() {
		return flats;
	}

	public void setFlats(List<Flat> flats) {
		this.flats = flats;
	}

	public Flat getFlat() {
		return flat;
	}

	public void setFlat(Flat flat) {
		this.flat = flat;
	}

	public Flat[] getSelectedFlats() {
		return selectedFlats;
	}

	public void setSelectedFlats(Flat[] selectedFlats) {
		this.selectedFlats = selectedFlats;
	}

}

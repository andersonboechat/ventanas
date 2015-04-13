package br.com.abware.accountmgm.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.primefaces.model.DualListModel;

import br.com.abware.accountmgm.bean.model.ModelDataModel;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Parking;

@ManagedBean
@ViewScoped
public class AdminBean extends BaseBean {

	private static Logger LOGGER = Logger.getLogger(AdminBean.class);

	private ModelDataModel<Flat> model;
	
	private DualListModel<Parking> parkings;

	private HashMap<String, Object> filters;	

	private Set<Integer> blocks;

	private int block;

	private Set<Integer> numbers;

	private int number;

	private List<Flat> flats;

	private Flat flat;

	private Flat[] selectedFlats;

	@PostConstruct
	public void init() {
		try {
			flats = flatService.getFlats(personService.getPerson());
			model = new ModelDataModel<Flat>(flats);

			blocks = new TreeSet<Integer>();
			numbers = new TreeSet<Integer>();
			for (Flat flat : flats) {
				blocks.add(flat.getBlock());
				numbers.add(flat.getNumber());
			}

			filters = new HashMap<String, Object>();
			flat = new Flat();			
			parkings = new DualListModel<Parking>(parkingService.getNotOwnedParkings(), new ArrayList<Parking>());
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
		parkings.getTarget().clear();
	}

	public void onFlatSave() {
		try {
			Flat f;

			if (flat.getId() == 0) {
				f = flatService.register(flat);
				model.addModel(f);
			} else {
				f = flatService.update(flat);
				model.setModel(f);
			}

			for (Parking parking : parkings.getTarget()) {
				parking.setOwnerDomain(f);
				parkingService.update(parking);
			}

			for (Parking parking : parkings.getSource()) {
				if (parking.getOwnerDomain() != null) {
					parking.setOwnerDomain(null);
					parkingService.update(parking);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
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
			BeanUtils.copyProperties(flat, model.getRowData());
			parkings.getTarget().clear();
			parkings.getTarget().addAll(parkingService.getOwnedParkings(flat));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ModelDataModel<Flat> getModel() {
		return model;
	}

	public DualListModel<Parking> getParkings() {
		return parkings;
	}

	public void setParkings(DualListModel<Parking> parkings) {
		this.parkings = parkings;
	}

	public Set<Integer> getBlocks() {
		return blocks;
	}

	public void setBlocks(Set<Integer> blocks) {
		this.blocks = blocks;
	}

	public Integer getBlock() {
		return block;
	}

	public void setBlock(Integer block) {
		this.block = block;
	}

	public Set<Integer> getNumbers() {
		return numbers;
	}

	public void setNumbers(Set<Integer> numbers) {
		this.numbers = numbers;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
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

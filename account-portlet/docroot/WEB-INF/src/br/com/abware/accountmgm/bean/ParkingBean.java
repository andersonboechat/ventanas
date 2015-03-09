package br.com.abware.accountmgm.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import br.com.abware.accountmgm.bean.model.ModelDataModel;
import br.com.abware.accountmgm.model.Parking;
import br.com.abware.jcondo.core.model.Domain;
import br.com.abware.jcondo.core.model.Flat;

@ManagedBean
@ViewScoped
public class ParkingBean extends BaseBean {

	private static Logger LOGGER = Logger.getLogger(ParkingBean.class);

	private ModelDataModel<Parking> model;

	private HashMap<String, Object> filters;
	
	private Parking parking;
	
	private List<Flat> flats;

	public void init(List<Flat> flats) {
		try {
			ArrayList<Parking> parkings;
			this.flats = flats;

			if (!CollectionUtils.isEmpty(flats)) {
				parkings = new ArrayList<Parking>();

				for (Flat flat : flats) {
					parkings.addAll(parkingService.getParkings(flat));
				}
				
				model = new ModelDataModel<Parking>(parkings);
			}

			filters = new HashMap<String, Object>();
		} catch (Exception e) {
			LOGGER.error("", e);
		}
	}

	@SuppressWarnings("unchecked")
	public void onDomainSearch(Domain domain) throws Exception {
		List<Parking> parkings;
		filters.put("renterDomain", domain != null ? domain : null);
		model.filter(filters);
		parkings = (List<Parking>) model.getWrappedData();
		filters.clear();
		filters.put("ownerDomain", domain != null ? domain : null);
		model.filter(filters);
		((List<Parking>) model.getWrappedData()).addAll(parkings);
	}

	public ModelDataModel<Parking> getModel() {
		return model;
	}

	public Parking getParking() {
		return parking;
	}

	public void setParking(Parking parking) {
		this.parking = parking;
	}

	public List<Flat> getFlats() {
		return flats;
	}

	public void setFlats(List<Flat> flats) {
		this.flats = flats;
	}
}

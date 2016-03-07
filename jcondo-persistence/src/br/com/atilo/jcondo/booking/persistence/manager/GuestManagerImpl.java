package br.com.atilo.jcondo.booking.persistence.manager;

import br.com.abware.jcondo.booking.model.Guest;
import br.com.atilo.jcondo.booking.persistence.entity.GuestEntity;
import br.com.atilo.jcondo.core.persistence.manager.JCondoManager;

public class GuestManagerImpl extends JCondoManager<GuestEntity, Guest>{

	@Override
	protected Class<Guest> getModelClass() {
		return Guest.class;
	}

	@Override
	protected Class<GuestEntity> getEntityClass() {
		return GuestEntity.class;
	}

}

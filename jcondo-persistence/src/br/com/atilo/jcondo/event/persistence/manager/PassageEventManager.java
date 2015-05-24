package br.com.atilo.jcondo.event.persistence.manager;

import br.com.abware.jcondo.access.model.PassageEvent;
import br.com.atilo.jcondo.core.persistence.manager.JCondoManager;
import br.com.atilo.jcondo.event.persistence.entity.PassageEventEntity;

public class PassageEventManager extends JCondoManager<PassageEventEntity, PassageEvent> {

	@Override
	protected Class<PassageEvent> getModelClass() {
		return PassageEvent.class;
	}

	@Override
	protected Class<PassageEventEntity> getEntityClass() {
		return PassageEventEntity.class;
	}

}

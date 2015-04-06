package br.com.atilo.jcondo.occurrence.persistence.manager;

import br.com.abware.jcondo.crm.model.Answer;
import br.com.atilo.jcondo.core.persistence.manager.JCondoManager;
import br.com.atilo.jcondo.occurrence.persistence.entity.AnswerEntity;

public class AnswerManagerImpl extends JCondoManager<AnswerEntity, Answer> {

	@Override
	protected Class<Answer> getModelClass() {
		return Answer.class;
	}

	@Override
	protected Class<AnswerEntity> getEntityClass() {
		return AnswerEntity.class;
	}

}

package br.com.abware.complaintbook.persistence.manager;

import br.com.abware.complaintbook.persistence.entity.Answer;

public class AnswerManager extends BaseManager<Answer> {

	@Override
	protected Class<Answer> getEntityClass() {
		return Answer.class;
	}

}

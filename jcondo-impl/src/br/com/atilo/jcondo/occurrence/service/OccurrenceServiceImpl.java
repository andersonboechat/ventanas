package br.com.atilo.jcondo.occurrence.service;

import java.util.Date;
import java.util.List;

import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.crm.model.Answer;
import br.com.abware.jcondo.crm.model.Occurrence;
import br.com.atilo.jcondo.occurrence.persistence.manager.AnswerManagerImpl;
import br.com.atilo.jcondo.occurrence.persistence.manager.OccurrenceManagerImpl;

public class OccurrenceServiceImpl {

	private OccurrenceManagerImpl occurrenceManager = new OccurrenceManagerImpl();
	
	private AnswerManagerImpl answerManager = new AnswerManagerImpl();

	private String generateCode(Occurrence occurrence) {
		return String.valueOf(occurrence.getId() + occurrence.getDate().getTime());
	}
	
	protected Occurrence answer(Occurrence occurrence, boolean draft) throws Exception {
		if (occurrence.getAnswer() == null) {
			throw new Exception("occurrence answer not specified");
		}

		Occurrence o = occurrenceManager.findById(occurrence.getId());

		if (o == null) {
			throw new Exception("occurrence not exist");
		}

		Answer answer = o.getAnswer();

		if (answer != null) {
			if (draft && answer.getDate() != null) {	
				throw new Exception("occurrence already answered: " + occurrence);
			}
		} else {
			answer = new Answer();
			o.setAnswer(answer);
		}

		answer.setDate(draft ? null : new Date());
		answer.setText(occurrence.getAnswer().getText());
		answer.setPerson(occurrence.getAnswer().getPerson());

		answer = answerManager.save(answer);
		o.setAnswer(answer);

		return occurrenceManager.save(o);
	}

	public Occurrence answer(Occurrence occurrence) throws Exception {
		return answer(occurrence, true);
	}

	public Occurrence saveAsDraft(Occurrence occurrence) throws Exception {
		return answer(occurrence, false);
	}

	public List<Occurrence> getOccurrences(Person person) throws Exception {
		if (person == null) {
			return occurrenceManager.findAll();
		}
		return occurrenceManager.findOccurrences(person);
	}
	
	public Occurrence register(Occurrence occurrence) throws Exception {
		if (occurrenceManager.findById(occurrence.getId()) != null || 
				occurrenceManager.findOccurrence(occurrence.getCode()) != null) {
			throw new Exception("occurrence already exists");
		}

		occurrence.setId(0);
		occurrence.setDate(new Date());
		occurrence.setCode(generateCode(occurrence));
		occurrence.setAnswer(null);

		return occurrenceManager.save(occurrence);
	}

}

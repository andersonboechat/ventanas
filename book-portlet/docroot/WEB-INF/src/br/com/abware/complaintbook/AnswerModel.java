package br.com.abware.complaintbook;

import java.util.Date;

import org.apache.log4j.Logger;

import com.liferay.portal.model.User;

import br.com.abware.complaintbook.exception.BusinessException;
import br.com.abware.complaintbook.persistence.entity.Answer;
import br.com.abware.complaintbook.persistence.manager.AnswerManager;
import br.com.abware.complaintbook.util.BeanUtils;

public class AnswerModel {

	private static Logger logger = Logger.getLogger(AnswerModel.class);
	
	private static AnswerManager manager = new AnswerManager();
	
	private int id;

	private Date date;

	private String text;

	private User user;

	public AnswerModel() {
	}
	
	public static AnswerModel getAnswer(int id) throws BusinessException {
		logger.trace("Method in");

		AnswerModel answerModel = null;

		try {
			logger.debug("Id: " + id);
			Answer answer = manager.findById(id);

			logger.debug("Id: " + id + "; Answer: " + answer);

			answerModel = new AnswerModel();
			BeanUtils.copyProperties(answerModel, answer);
			answerModel.setUser(UserHelper.getUserById(answer.getUserId()));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage());
		}

		logger.info("Id: " + id + "; Answer: " + answerModel);

		logger.trace("Method out");		

		return answerModel;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}

package br.com.abware.complaintbook;

import java.util.Date;

import javax.validation.ConstraintViolationException;

import org.apache.log4j.Logger;

import com.liferay.portal.model.User;

import br.com.abware.complaintbook.exception.BusinessException;
import br.com.abware.complaintbook.exception.EmptyPropertyException;
import br.com.abware.complaintbook.persistence.entity.Answer;
import br.com.abware.complaintbook.persistence.manager.AnswerManager;
import br.com.abware.complaintbook.util.BeanUtils;

public class AnswerModel {

	private static Logger logger = Logger.getLogger(AnswerModel.class);
	
	private AnswerManager manager = new AnswerManager();
	
	private long id;

	private Date date;

	private String text;

	private User user;
	
	private boolean draft;

	public AnswerModel() {
		this.draft = true;
	}
	
	public AnswerModel getAnswer(long id) throws BusinessException {
		logger.trace("Method in");
		String owner = String.valueOf("AnswerModel.getAnswer");

		AnswerModel answerModel = null;

		try {
			manager.openManager(owner);
			logger.debug("Id: " + id);
			Answer answer = manager.findById(id);

			logger.debug("Id: " + id + "; Answer: " + answer);

			answerModel = new AnswerModel();
			BeanUtils.copyProperties(answerModel, answer);
			answerModel.setUser(UserHelper.getUserById(answer.getUserId()));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage());
		} finally {
			manager.closeManager(owner);
		}

		logger.info("Id: " + id + "; Answer: " + answerModel);

		logger.trace("Method out");		

		return answerModel;
	}
	
	public void doAnswer(AnswerModel answerModel) throws BusinessException {
		logger.trace("Method in");
		String owner = String.valueOf("AnswerModel.doAnswer");
		long userId = UserHelper.getLoggedUser().getUserId();

		try {
			manager.openManager(owner);
			Answer answer = new Answer();
			answer.setUserId(userId);
			BeanUtils.copyProperties(answer, answerModel);
			answer = manager.save(answer, userId);
			BeanUtils.copyProperties(answerModel, answer);
			answerModel.setUser(UserHelper.getUserById(answer.getUserId()));
		} catch (ConstraintViolationException e) {
			logger.warn(e.getMessage(), e);
			throw new EmptyPropertyException(e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage());
		} finally {
			manager.closeManager(owner);
		}

		logger.info("Answer done: " + answerModel.getId());

		logger.trace("Method out");
	}	
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
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

	public boolean isDraft() {
		return draft;
	}

	public void setDraft(boolean draft) {
		this.draft = draft;
	}
}

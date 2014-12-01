package br.com.abware.complaintbook;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.validation.ConstraintViolationException;

import org.apache.log4j.Logger;

import com.liferay.portal.model.User;

import br.com.abware.complaintbook.exception.BusinessException;
import br.com.abware.complaintbook.exception.EmptyPropertyException;
import br.com.abware.complaintbook.exception.OccurrenceAlreadyDoneException;
import br.com.abware.complaintbook.persistence.entity.Occurrence;
import br.com.abware.complaintbook.persistence.manager.OccurrenceManager;
import br.com.abware.complaintbook.util.BeanUtils;

public class OccurrenceModel {

	private static Logger logger = Logger.getLogger(OccurrenceModel.class);
	
	private OccurrenceManager manager = new OccurrenceManager();
	
	private long id;
	
	private String code;

	private Date date;

	private String text;

	private OccurenceType type;

	private User user;

	private AnswerModel answer;

	public OccurrenceModel() {
	}

	public OccurrenceModel(OccurenceType type, User user) {
		this.type = type;
		this.user = user;
	}
	
	public OccurrenceModel getOccurrence(long id) throws BusinessException {
		logger.trace("Method in");
		String owner = String.valueOf("OccurrenceModel.getUserOccurrence");

		OccurrenceModel occurrenceModel = null;
		Occurrence occurrence = null;

		try {
			manager.openManager(owner);
			occurrence = manager.findById(id);
			occurrenceModel = new OccurrenceModel();
			BeanUtils.copyProperties(occurrenceModel, occurrence);
			occurrenceModel.setUser(UserHelper.getUserById(occurrence.getUserId()));
			if (occurrence.getAnswer() != null) {
				occurrenceModel.getAnswer().setUser(UserHelper.getUserById(occurrence.getAnswer().getUserId()));
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BusinessException("Unknown Exception. Check log files");
		} finally {
			manager.closeManager(owner);
		}

		logger.info("Id: " + id + "; Occurrence: " + occurrence);
		
		logger.trace("Method in");		
		
		return occurrenceModel;
	}
	
	public List<OccurrenceModel> getOccurrences() throws BusinessException {
		logger.trace("Method in");
		String owner = String.valueOf("OccurrenceModel.getOccurrences");

		List<OccurrenceModel> occurrences = new ArrayList<OccurrenceModel>();
		OccurrenceModel occurrenceModel;

		try {
			manager.openManager(owner);
			for (Occurrence occurrence : manager.findAll()) {
				logger.debug("OccurrenceEntity: " + occurrence);

				occurrenceModel = new OccurrenceModel();
				BeanUtils.copyProperties(occurrenceModel, occurrence);
				occurrenceModel.setUser(UserHelper.getUserById(occurrence.getUserId()));
				if (occurrence.getAnswer() != null) {
					occurrenceModel.getAnswer().setUser(UserHelper.getUserById(occurrence.getAnswer().getUserId()));
				}
				occurrences.add(occurrenceModel);

				logger.debug("OccurrenceModel: " + occurrenceModel);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BusinessException("Unknown Exception. Check log files");
		} finally {
			manager.closeManager(owner);
		}

		logger.trace("Method out");

		return occurrences;
	}
	
	public List<OccurrenceModel> getUserOccurrences(long userId) throws BusinessException {
		logger.trace("Method in");
		String owner = String.valueOf("OccurrenceModel.getUserOccurrences");

		List<OccurrenceModel> occurrences = new ArrayList<OccurrenceModel>();
		OccurrenceModel occurrenceModel;

		try {
			manager.openManager(owner);
			for (Occurrence occurrence : manager.findOccurrencesByUserId(userId)) {
				logger.debug("OccurrenceEntity: " + occurrence);

				occurrenceModel = new OccurrenceModel();
				BeanUtils.copyProperties(occurrenceModel, occurrence);
				occurrenceModel.setUser(UserHelper.getUserById(occurrence.getUserId()));
				if (occurrence.getAnswer() != null) {
					occurrenceModel.getAnswer().setUser(UserHelper.getUserById(occurrence.getAnswer().getUserId()));
				}
				occurrences.add(occurrenceModel);

				logger.debug("OccurrenceModel: " + occurrenceModel);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BusinessException("Unknown Exception. Check log files");
		} finally {
			manager.closeManager(owner);
		}

		logger.info("User: " + userId + "; Occurrences: " + occurrences.size());

		logger.trace("Method out");

		return occurrences;
	}
	
	public void doOccurrence(OccurrenceModel occurrenceModel) throws BusinessException {
		logger.trace("Method in");
		String owner = String.valueOf("OccurrenceModel.doOccurrence");

		logger.debug("Checking whether id is not zero");
		if (occurrenceModel.getId() != 0) {
			logger.warn("Ocorrencia ja registrada");
			throw new OccurrenceAlreadyDoneException("Ocorrencia já registrada");
		}

		try {
			manager.openManager(owner);
			Occurrence occurrence = new Occurrence(occurrenceModel.getUser().getUserId());
			occurrenceModel.setCode(generateCode());
			occurrenceModel.setDate(new Date());
			BeanUtils.copyProperties(occurrence, occurrenceModel);
			occurrence = manager.save(occurrence, UserHelper.getLoggedUser().getUserId());
			BeanUtils.copyProperties(occurrenceModel, occurrence);

		} catch (ConstraintViolationException e) {
			logger.warn(e.getMessage(), e);
			throw new EmptyPropertyException(e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage());
		} finally {
			manager.closeManager(owner);
		}

		logger.info("Occurrence done: " + occurrenceModel.getCode());

		logger.trace("Method out");
	}

	public void doAnswer(OccurrenceModel occurrenceModel) throws BusinessException {
		logger.trace("Method in");
		String owner = String.valueOf("OccurrenceModel.doAnswer");

		try {
			manager.openManager(owner);
			Occurrence occurrence = new Occurrence(occurrenceModel.getUser().getUserId());
			BeanUtils.copyProperties(occurrence, occurrenceModel);
			occurrence = manager.save(occurrence, UserHelper.getLoggedUser().getUserId());
			BeanUtils.copyProperties(occurrenceModel, occurrence);
		} catch (ConstraintViolationException e) {
			logger.warn(e.getMessage(), e);
			throw new EmptyPropertyException(e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage());
		} finally {
			manager.closeManager(owner);
		}

		logger.info("Answer done: " + occurrenceModel.getCode());

		logger.trace("Method out");
	}	
	
	private static String generateCode() {
		return String.valueOf(Calendar.getInstance().getTimeInMillis());
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
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

	public OccurenceType getType() {
		return type;
	}

	public void setType(OccurenceType type) {
		this.type = type;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public AnswerModel getAnswer() {
		return answer;
	}

	public void setAnswer(AnswerModel answer) {
		this.answer = answer;
	}

}

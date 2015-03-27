package br.com.atilo.jcondo.booking.persistence.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import br.com.abware.jcondo.booking.model.Room;
import br.com.abware.jcondo.booking.model.RoomBooking;
import br.com.abware.jcondo.core.model.Person;
import br.com.atilo.jcondo.booking.persistence.entity.RoomBookingEntity;
import br.com.atilo.jcondo.core.persistence.manager.JCondoManager;

public class RoomBookingManagerImpl extends JCondoManager<RoomBookingEntity, RoomBooking> {

	@Override
	protected Class<RoomBooking> getModelClass() {
		return RoomBooking.class;
	}

	@Override
	protected Class<RoomBookingEntity> getEntityClass() {
		return RoomBookingEntity.class;
	}

	@SuppressWarnings("unchecked")
	public List<RoomBooking> findByPeriod(Room room, Date fromDate, Date toDate) throws Exception {
		String key = generateKey();
		try {
			openManager(key);
			String queryString = "FROM RoomBookingEntity WHERE resource.id = :roomId AND (beginDate BETWEEN :fromDate AND :toDate) AND (endDate BETWEEN :fromDate AND :toDate)";
			Query query = em.createQuery(queryString);
			query.setParameter("roomId", room.getId());
			query.setParameter("fromDate", fromDate);
			query.setParameter("toDate", toDate);
			return getModels(query.getResultList());
		} catch (NoResultException e) {
			return new ArrayList<RoomBooking>();
		} finally {
			closeManager(key);
		}		
	}

	@SuppressWarnings("unchecked")
	public List<RoomBooking> findByPerson(Person person) throws Exception {
		String key = generateKey();
		try {
			openManager(key);
			String queryString = "FROM RoomBookingEntity WHERE person.id = :personId ORDER BY beginDate DESC";
			Query query = em.createQuery(queryString);
			query.setParameter("personId", person.getId());
			return getModels(query.getResultList());
		} catch (NoResultException e) {
			return new ArrayList<RoomBooking>();
		} finally {
			closeManager(key);
		}		
	}

}
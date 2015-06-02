package br.com.atilo.jcondo.booking.persistence.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import br.com.abware.jcondo.booking.model.Guest;
import br.com.abware.jcondo.booking.model.Room;
import br.com.abware.jcondo.booking.model.RoomBooking;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.exception.PersistenceException;
import br.com.atilo.jcondo.booking.persistence.entity.GuestEntity;
import br.com.atilo.jcondo.booking.persistence.entity.RoomBookingEntity;
import br.com.atilo.jcondo.commons.BeanUtils;
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
	
	@Override
	protected RoomBookingEntity getEntity(RoomBooking model) throws Exception {
		try {
			RoomBookingEntity roomBooking = super.getEntity(model);

			if (model.getGuests() != null) {
				List<GuestEntity> guests = new ArrayList<GuestEntity>();
				for (Guest guest : model.getGuests()) {
					GuestEntity entity = new GuestEntity();
					BeanUtils.copyProperties(entity, guest);
					entity.setBooking(roomBooking);
					entity.setUpdateDate(new Date());
					entity.setUpdateUser(helper.getUserId());
					guests.add(entity);
				}
				roomBooking.setGuests(guests);
			}

			return roomBooking;
		} catch (Exception e) {
			throw new PersistenceException(e, "psn.mgr.get.entity.fail");
		}
		
	}
	
	@Override
	protected RoomBooking getModel(RoomBookingEntity entity) throws Exception {
		try {
			RoomBooking roomBooking = super.getModel(entity);
			
			List<Guest> guests = new ArrayList<Guest>();
			for (GuestEntity guest : entity.getGuests()) {
				Guest model = new Guest();
				BeanUtils.copyProperties(model, guest);
				guests.add(model);
			}
			roomBooking.setGuests(guests);
			
			return roomBooking;
		} catch (Exception e) {
			throw new PersistenceException(e, "psn.mgr.get.model.failed");
		}
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
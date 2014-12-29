package br.com.abware.agenda.persistence.manager;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import br.com.abware.agenda.persistence.entity.Booking;
import br.com.abware.agenda.persistence.entity.Room;

public class BookingManager extends BaseManager<Booking>{

	@SuppressWarnings("unchecked")
	public List<Booking> findBookingsByPeriod(Room room, Date startDate, Date endDate) {
		String queryString = "FROM Booking WHERE room = :room AND date BETWEEN :startDate AND :endDate";
		Query query = em.createQuery(queryString);

		query.setParameter("room", room);
		query.setParameter("startDate", startDate);
		query.setParameter("endDate", endDate);

		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Booking> findBookingsByUserId(long userId) {
		String queryString = "FROM Booking WHERE userId = :userId ORDER BY date desc";
		Query query = em.createQuery(queryString);

		query.setParameter("userId", userId);

		return query.getResultList();
	}
	
	@Override
	protected Class<Booking> getEntityClass() {
		return Booking.class;
	}
	
}

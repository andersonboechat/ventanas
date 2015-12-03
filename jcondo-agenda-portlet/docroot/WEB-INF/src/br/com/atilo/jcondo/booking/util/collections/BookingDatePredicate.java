package br.com.atilo.jcondo.booking.util.collections;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.time.DateUtils;
import org.primefaces.model.ScheduleEvent;

public class BookingDatePredicate implements Predicate {

	private Date bookingDate;
	
	public BookingDatePredicate(Date bookingDate) {
		this.bookingDate = bookingDate;
	}
	
	@Override
	public boolean evaluate(Object obj) {
		if (obj != null && obj instanceof ScheduleEvent) {
			Date startDate = DateUtils.truncate(((ScheduleEvent) obj).getStartDate(), Calendar.DAY_OF_MONTH);
			Date endDate = DateUtils.truncate(((ScheduleEvent) obj).getEndDate(), Calendar.DAY_OF_MONTH);
			return bookingDate.getTime() >= startDate.getTime() && bookingDate.getTime() <= endDate.getTime();
		}

		return false;
	}

}

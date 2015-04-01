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
			Date date = ((ScheduleEvent) obj).getStartDate();
			date = DateUtils.truncate(date, Calendar.DAY_OF_MONTH);
			return date.equals(bookingDate);
		}

		return false;
	}

}

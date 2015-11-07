package br.com.atilo.jcondo.booking.bean;

import java.util.Collections;
import java.util.Comparator;
import java.util.Observable;
import java.util.Observer;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;

import br.com.abware.jcondo.booking.model.RoomBooking;
import br.com.atilo.jcondo.booking.util.BookingEvent;
import br.com.atilo.jcondo.booking.util.EventType;
import br.com.atilo.jcondo.commons.collections.BeanSorter;

@ManagedBean
@ViewScoped
public class BookingBean extends BaseBean implements Observer {

	private static final Logger LOGGER = Logger.getLogger(BookingBean.class);

	@ManagedProperty(value="#{calendarBean}")
	private CalendarBean calendarBean;

	@ManagedProperty(value="#{historyBean}")
	private HistoryBean historyBean;

	@ManagedProperty(value="#{roomBean}")
	private RoomBean roomBean;

	@PostConstruct
	public void init() {
		try {
			calendarBean.addObserver(this);
		} catch (Exception e) {
			LOGGER.fatal("failure on booking application loading", e);
		}
	}

	@Override
	public void update(Observable observable, Object object) {
		try {
			if (observable instanceof CalendarBean) {
				if (object instanceof BookingEvent) {
					BookingEvent event = (BookingEvent) object;
					if (event.getType() == EventType.BOOK) {
						historyBean.getBookings().add((RoomBooking) event.getSource());
						Collections.sort(historyBean.getBookings(), new BeanSorter<RoomBooking>("beginDate", BeanSorter.DESCENDING_ORDER));
					}

					if (event.getType() == EventType.BOOK_DELETE) {
						historyBean.getBookings().remove((RoomBooking) event.getSource());	
					}

					if (event.getType() == EventType.BOOK_CANCEL) {
						int index = historyBean.getBookings().indexOf((RoomBooking) event.getSource());
						historyBean.getBookings().set(index, (RoomBooking) event.getSource());
					}
				}
			}
		} catch (Exception e) {
			LOGGER.warn("fail to update booking history");
		}
	}

	public CalendarBean getCalendarBean() {
		return calendarBean;
	}

	public void setCalendarBean(CalendarBean calendarBean) {
		this.calendarBean = calendarBean;
	}

	public HistoryBean getHistoryBean() {
		return historyBean;
	}

	public void setHistoryBean(HistoryBean historyBean) {
		this.historyBean = historyBean;
	}

	public RoomBean getRoomBean() {
		return roomBean;
	}

	public void setRoomBean(RoomBean roomBean) {
		this.roomBean = roomBean;
	}

}

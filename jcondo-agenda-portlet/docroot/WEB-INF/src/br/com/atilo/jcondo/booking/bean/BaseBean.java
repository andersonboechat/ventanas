package br.com.atilo.jcondo.booking.bean;

import br.com.atilo.jcondo.booking.service.RoomBookingServiceImpl;
import br.com.atilo.jcondo.booking.service.RoomServiceImpl;
import br.com.atilo.jcondo.core.service.FlatServiceImpl;
import br.com.atilo.jcondo.core.service.PersonServiceImpl;


public abstract class BaseBean {

	protected static PersonServiceImpl personService = new PersonServiceImpl();
	
	protected static FlatServiceImpl flatService = new FlatServiceImpl();
	
	protected static RoomServiceImpl roomService = new RoomServiceImpl();
	
	protected static RoomBookingServiceImpl bookingService = new RoomBookingServiceImpl();

}

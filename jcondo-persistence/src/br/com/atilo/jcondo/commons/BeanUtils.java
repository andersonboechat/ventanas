package br.com.atilo.jcondo.commons;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;

import br.com.abware.jcondo.booking.model.Bookable;
import br.com.abware.jcondo.booking.model.Room;
import br.com.abware.jcondo.booking.model.RoomBooking;
import br.com.abware.jcondo.core.PersonStatus;
import br.com.abware.jcondo.core.model.Administration;
import br.com.abware.jcondo.core.model.Domain;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Image;
import br.com.abware.jcondo.core.model.Membership;
import br.com.abware.jcondo.core.model.Parking;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.core.model.Vehicle;
import br.com.atilo.jcondo.booking.persistence.entity.RoomBookingEntity;
import br.com.atilo.jcondo.booking.persistence.entity.RoomEntity;
import br.com.atilo.jcondo.core.persistence.entity.AdministrationEntity;
import br.com.atilo.jcondo.core.persistence.entity.DomainEntity;
import br.com.atilo.jcondo.core.persistence.entity.FlatEntity;
import br.com.atilo.jcondo.core.persistence.entity.MembershipEntity;
import br.com.atilo.jcondo.core.persistence.entity.ParkingEntity;
import br.com.atilo.jcondo.core.persistence.entity.PersonEntity;
import br.com.atilo.jcondo.core.persistence.entity.VehicleEntity;

public class BeanUtils extends org.apache.commons.beanutils.BeanUtils {

	static {
		 ConvertUtilsBean cub = BeanUtilsBean.getInstance().getConvertUtils();
		 BeanConverter bc = new BeanConverter();
		 cub.register(bc, Membership.class);
		 cub.register(bc, MembershipEntity.class);
		 cub.register(bc, Person.class);
		 cub.register(bc, PersonEntity.class);
		 cub.register(bc, Administration.class);
		 cub.register(bc, AdministrationEntity.class);
		 cub.register(bc, Flat.class);
		 cub.register(bc, FlatEntity.class);
		 cub.register(bc, Vehicle.class);
		 cub.register(bc, VehicleEntity.class);
		 cub.register(bc, Parking.class);
		 cub.register(bc, ParkingEntity.class);
		 cub.register(bc, Room.class);
		 cub.register(bc, Bookable.class);
		 cub.register(bc, RoomEntity.class);
		 cub.register(bc, RoomBooking.class);
		 cub.register(bc, RoomBookingEntity.class);
		 cub.register(bc, Image.class);

		 DomainConverter dc = new DomainConverter();
		 cub.register(dc, DomainEntity.class);
		 cub.register(dc, Domain.class);

		 cub.register(new PersonStatusConverter(), PersonStatus.class);
	}
	
	public static void copyProperties(Object dest, Object orig) throws IllegalAccessException, InvocationTargetException {
		BeanUtilsBean.getInstance().copyProperties(dest, orig);
	}
}

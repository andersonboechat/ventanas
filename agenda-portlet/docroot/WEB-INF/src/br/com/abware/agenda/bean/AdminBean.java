package br.com.abware.agenda.bean;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.primefaces.event.ItemSelectEvent;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;

import com.liferay.compat.portal.util.PortalUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import br.com.abware.agenda.BookingModel;
import br.com.abware.agenda.BookingStatus;
import br.com.abware.agenda.Image;
import br.com.abware.agenda.RoomModel;

@ManagedBean
@SessionScoped
public class AdminBean extends BaseBean {

	private static final Logger LOGGER = Logger.getLogger(AdminBean.class);

	private static final int ALL_MONTHS = -1;

	private List<BookingModel> bookings;

	private CartesianChartModel barChart;

	private RoomModel room;
	
	private BookingModel booking;
	
	private Image image;
	
	private int year;
	
	private List<Integer> years = new ArrayList<Integer>();
	
	private int month;

	public AdminBean() {
		Calendar calendar = Calendar.getInstance();
	
		year = calendar.get(Calendar.YEAR);
		month = ALL_MONTHS;

		for (int i = 2011; i <= year; i++) {
			years.add(i);
		}

		room = rooms.get(0);
		image = room.getImages().get(0);
		bookings = new ArrayList<BookingModel>();

		buildChart();
	}

	private void buildChart() {
		Calendar calendar = Calendar.getInstance();
		CartesianChartModel model = new CartesianChartModel();
		ChartSeries chartSeries = new ChartSeries();
		BookingModel booking = new BookingModel();
		List<BookingModel> bookings = new ArrayList<BookingModel>();

		for (int i = 0; i < 12; i++) {
			bookings = booking.getBookings(i, year);

			if (month == i || month == ALL_MONTHS) {
				this.bookings.addAll(bookings);
			}

			calendar.set(year, i, 1);

			chartSeries.set(calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()), bookings.size());
		}

		model.addSeries(chartSeries);

		barChart = model;
	}

	public void onSelectMonth(ItemSelectEvent event) {
		try {
			month = event.getItemIndex();
			bookings = new BookingModel().getBookings(month, year);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void onSearch() {
		try {		
			month = ALL_MONTHS;
			buildChart();
		} catch (Exception e) {
			// TODO: handle exception
		}		
	}	

	public void onBookingCancel(Integer index) {
		try {
			BookingModel bm = bookings.get(index.intValue());
			//bm.updateStatus(bm, BookingStatus.CANCELLED);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void onBookingDischarge(Integer index) {
		try {
			BookingModel bm = bookings.get(index.intValue());
			//bm.updateStatus(bm, BookingStatus.PAID);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public String displayBookingsHeader() {
		String monthName;

		if (month == ALL_MONTHS) {
			monthName = ResourceBundleUtil.getString(rb, "all.months"); 
		} else {
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.MONTH, month);
			monthName = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()); 
		}

		return ResourceBundleUtil.getString(rb, Locale.getDefault(), "bookings.header", new Object[] {monthName, year}); 
	}
	
    public void postProcessXLS(Object document) {
    	try {
	    	HSSFWorkbook wb = (HSSFWorkbook) document;
//	        HSSFSheet sheet = wb.getSheetAt(0);
//	        CellStyle style = wb.createCellStyle();
//	        style.setFillBackgroundColor(IndexedColors.AQUA.getIndex());
//	
//	        for (Row row : sheet) {
//	            for (Cell cell : row) {
//	                cell.setCellValue(cell.getStringCellValue().toUpperCase());
//	                cell.setCellStyle(style);
//	            }
//	        }
	        
	        PortletResponse portletResponse; 
	        portletResponse = (PortletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
	        HttpServletResponse res = PortalUtil.getHttpServletResponse(portletResponse);
			res.setHeader("Content-Disposition", "attachment; filename=\"" + "alugueis-"+ year +".xls" + "\"");
			res.setHeader("Content-Transfer-Encoding", "binary");
			res.setContentType("application/octet-stream");
			res.flushBuffer();
		
			OutputStream out = res.getOutputStream();
			wb.write(out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}        
    }

	public CartesianChartModel getBarChart() {
		return barChart;
	}

	public void setBarChart(CartesianChartModel barChart) {
		this.barChart = barChart;
	}

	public List<BookingModel> getBookings() {
		return bookings;
	}

	public void setBookings(List<BookingModel> bookings) {
		this.bookings = bookings;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public List<Integer> getYears() {
		return years;
	}

	public void setYears(List<Integer> years) {
		this.years = years;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public RoomModel getRoom() {
		return room;
	}

	public void setRoom(RoomModel room) {
		this.room = room;
	}

	public BookingModel getBooking() {
		return booking;
	}

	public void setBooking(BookingModel booking) {
		this.booking = booking;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

}

package br.com.abware.agenda;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CurrencyFormat {

	public static void main(String[] args) throws ParseException {
		//System.out.print(java.text.NumberFormat.getCurrencyInstance().format(38));
		Calendar c = Calendar.getInstance();
		c.setTime(Date.valueOf("2015-01-20"));
		c.add(Calendar.DAY_OF_YEAR, -30);
		System.out.print(new java.text.SimpleDateFormat("MMMMM 'de' yyyy").format(java.sql.Date.valueOf("2015-01-20").getTime() - 2592000000L));
		System.out.print(new java.text.SimpleDateFormat("MMMMM 'de' yyyy").format(java.sql.Date.valueOf("2015-01-20")));
	}

}

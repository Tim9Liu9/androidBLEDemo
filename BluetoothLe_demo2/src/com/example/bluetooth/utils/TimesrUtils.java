package com.example.bluetooth.utils;

import java.util.Calendar;

public class TimesrUtils {

	//获得当前日历的0点时间 
	public static int getTimesMorning(Calendar cal )
	{ 		
		cal.set(Calendar.HOUR_OF_DAY, 0); 
		cal.set(Calendar.SECOND, 0); 
		cal.set(Calendar.MINUTE, 0); 
		cal.set(Calendar.MILLISECOND, 0); 
		return (int) (cal.getTimeInMillis()/1000); 
	}
		
	//获得当前日历的24点时间 
	public static int getTimesNight(Calendar cal)
	{ 	
		cal.set(Calendar.HOUR_OF_DAY, 24); 
		cal.set(Calendar.SECOND, 0); 
		cal.set(Calendar.MINUTE, 0); 
		cal.set(Calendar.MILLISECOND, 0); 
		return (int) (cal.getTimeInMillis()/1000); 
	}
}

package com.sylphcorps.utils;

import java.util.Date;
import java.util.TimeZone;

import org.springframework.stereotype.Component;

@Component
public class DateConverter {
	
	 public Long getGMTTime() 
	 {
		    Date now = new Date();
	        TimeZone.setDefault( TimeZone.getTimeZone("GMT"));
	        Long currentTimeStamp=now.getTime();
	        System.out.println(now);	 
		 return currentTimeStamp;
		 
	 }

}

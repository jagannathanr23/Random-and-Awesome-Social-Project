package com.jatinhariani.rasp.utils;

import android.annotation.SuppressLint;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class TimeAgo {
	
	@SuppressLint("NewApi")
	public static String getTimeAgo(Date d){
		
		Date now = new Date();
		String s="";
		TimeZone tz= TimeZone.getDefault();
		long mils = tz.getOffset(now.getTime());
		if(TimeUnit.MILLISECONDS.toDays(now.getTime() - mils - d.getTime())>0){
			return TimeUnit.MILLISECONDS.toDays(now.getTime() -mils - d.getTime())+" days ago";
		}
		if(TimeUnit.MILLISECONDS.toHours(now.getTime()-mils  - d.getTime())>0){
			return TimeUnit.MILLISECONDS.toHours(now.getTime() -mils - d.getTime())+" hours ago";
		}
		if(TimeUnit.MILLISECONDS.toMinutes(now.getTime()-mils  - d.getTime())>0){
			return TimeUnit.MILLISECONDS.toMinutes(now.getTime()-mils  - d.getTime())+" minutes ago";
		}
		if(TimeUnit.MILLISECONDS.toMillis(now.getTime() - d.getTime()) > 0){
			return "seconds ago";
		}
		return s;
	}

}

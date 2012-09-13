package de.minestar.autorestart.core;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class DateTimeHelper {
    public static Long getOnlyTime(Date date) {
        DateFormat df = DateFormat.getTimeInstance();
        String textTime = df.format(date);
        return getOnlyTimeLong(textTime);
    }
    
    public static long getOnlyTimeLong(String timeText) {
        long result;
        long hours, minutes;
        String[] timeFragments = timeText.split(":");
        hours = Long.parseLong(timeFragments[0]);
        minutes = Long.parseLong(timeFragments[1]);
        
        result = TimeUnit.HOURS.toMillis(hours);
        result += TimeUnit.MINUTES.toMillis(minutes);
        return result;
    }
    
    public static Date getOnlyDate(Date date) {
        DateFormat df = DateFormat.getDateInstance();
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        String textDate = df.format(date);
        Date result = null;
        try {
            result = df.parse(textDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    public static long getTimeDifference(Long time1, Long time2) {
        long result;
        
        if (time2 >= time1) {
            System.out.println("time2 >= time1");
            result = time2 - time1;
        } else {
            System.out.println("time2 < time1");
            System.out.println("time1      = " + time1);
            System.out.println("time2      = " + time2);
            System.out.println("Day in ms >= " + TimeUnit.DAYS.toMillis(1));
            result = time2 + TimeUnit.DAYS.toMillis(1) - time1;
        }
        return result;
    }
}

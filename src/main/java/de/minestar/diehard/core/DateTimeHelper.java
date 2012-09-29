package de.minestar.diehard.core;

import java.text.DateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateTimeHelper {
    public static long getOnlyTime(Date date) {
        // retrieve only time part and return as milliseconds sind epoch
        DateFormat df = DateFormat.getTimeInstance();
        String textTime = df.format(date);
        return getOnlyTimeLong(textTime);
    }

    public static long getOnlyTimeLong(String timeText) {
        long result;
        long hours, minutes;
        // convert times from config file to milliseconds since epoch
        String[] timeFragments = timeText.split(":");
        try {
            hours = Long.parseLong(timeFragments[0]);
            minutes = Long.parseLong(timeFragments[1]);

            if ((hours >= 0) && (hours <= 23) && (minutes >= 0) && (minutes <= 59)) {
                result = TimeUnit.HOURS.toMillis(hours);
                result += TimeUnit.MINUTES.toMillis(minutes);
            } else {
                result = -2;
            }
        } catch (NumberFormatException e) {
            result = -1;
        }
        return result;
    }

    public static long getTimeDifference(long startTime, long endtime) {
        long result;
        if (endtime >= startTime) {
            result = endtime - startTime;
        } else {
            //
            result = endtime + TimeUnit.DAYS.toMillis(1) - startTime;
        }
        return result;
    }

    public static String convertMillisToTime(long millis) {
        long hours, minutes;
        // convert milliseconds since epoch to time in format HH:mm
        hours = TimeUnit.MILLISECONDS.toHours(millis);
        minutes = TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(hours);
        return String.format("%02d:%02d", hours, minutes);
    }
}

package de.minestar.diehard.core;

import java.text.DateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateTimeHelper {
    public static Time getOnlyTime(Date date) {
        // retrieve only time part and return as milliseconds sind epoch
        DateFormat df = DateFormat.getTimeInstance();
        return new Time(df.format(date));
    }

    public static Time getOnlyTimeLong(String timeText) {
        return new Time(timeText);
    }

    public static Time getTimeDifference(Time startTime, Time endTime) {
        int result;
        if (endTime.compareTo(startTime) >= 0) {
            Time t = endTime.substract(startTime);
            result = t.toMinutes();
        } else {
            result = endTime.toMinutes() + (int) TimeUnit.DAYS.toMinutes(1) - startTime.toMinutes();
        }
        return new Time(result);
    }

    public static String convertMillisToTime(long millis) {
        long hours, minutes;
        // convert milliseconds since epoch to time in format HH:mm
        hours = TimeUnit.MILLISECONDS.toHours(millis);
        minutes = TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(hours);
        return String.format("%02d:%02d", hours, minutes);
    }
}

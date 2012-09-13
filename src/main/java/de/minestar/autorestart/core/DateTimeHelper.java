package de.minestar.autorestart.core;

import java.text.DateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateTimeHelper {
    public static long getOnlyTime(Date date) {
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

    public static long getTimeDifference(long time1, long time2) {
        long result;

        if (time2 >= time1) {
            result = time2 - time1;
        } else {
            result = time2 + TimeUnit.DAYS.toMillis(1) - time1;
        }
        return result;
    }

    public static String convertMillisToTime(long millis) {
        long hours, minutes;
        hours = TimeUnit.MILLISECONDS.toHours(millis);
        minutes = TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(hours);
        return String.format("%02d:%02d", hours, minutes);
    }
}

package de.minestar.autorestart.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import de.minestar.autorestart.threads.CheckThread;
import de.minestar.minestarlibrary.config.MinestarConfig;
import de.minestar.minestarlibrary.utils.ConsoleUtils;

public class Settings {

    /* VALUES */

    private static List<Calendar> restartTimes;
    private static List<Calendar> warningTimes;

    /* USED FOR SETTING */

    private static MinestarConfig config;
    private static File configFile;

    private Settings() {

    }

    public static boolean init(File dataFolder, String pluginName, String pluginVersion) {
        configFile = new File(dataFolder, "config.yml");
        try {
            // LOAD EXISTING CONFIG FILE
            if (configFile.exists()) {
                config = new MinestarConfig(configFile, pluginName, pluginVersion);
                // CREATE A DEFAUL ONE
            } else {
                config = MinestarConfig.copyDefault(Settings.class.getResourceAsStream("/config.yml"), configFile);
            }
            loadValues();
            return true;

        } catch (Exception e) {
            ConsoleUtils.printException(e, pluginName, "Can't load the settings from " + configFile);
            return false;
        }
    }

    private static void loadValues() {
        /* Restart times */
        loadRestartTimes();
        /* Warning times */
        loadWarningTimes();
    }

    private static Calendar convertTimeTextToCalendar(String timeText) {
        int hours = -1, minutes = -1;
        Calendar cal = new GregorianCalendar(0, 0, 0, 0, 0);
        String[] splitTimeText = timeText.split(":");
        if (splitTimeText.length == 2) {

            try {
                hours = Integer.parseInt(splitTimeText[0]);
                minutes = Integer.parseInt(splitTimeText[1]);
            } catch (NumberFormatException e) {
                // timeText contains illegal characters
            }
        }

        if ((hours >= 0) && (hours <= 23)) {
            if ((minutes >= 0) && (minutes <= 59)) {
                cal.set(Calendar.HOUR_OF_DAY, hours);
                cal.set(Calendar.MINUTE, minutes);
            }
        }
        return cal;
    }

    private static void loadRestartTimes() {
        List<String> listRestartTimeText = config.getStringList("Restart Times");

        restartTimes = new ArrayList<Calendar>();
        for (String time : listRestartTimeText) {
            Calendar cal = convertTimeTextToCalendar(time);
            restartTimes.add(cal);
        }
        Collections.sort(restartTimes);
        ConsoleUtils.printInfo(AutoRestartCore.NAME, "Loaded restart times");
        for (Calendar cal : restartTimes) {
            ConsoleUtils.printInfo(AutoRestartCore.NAME, CheckThread.printCalendarTime(cal));
        }
    }
    
    private static void loadWarningTimes() {
        List<String> listWarningTimeText = config.getStringList("Warning Times");

        warningTimes = new ArrayList<Calendar>();
        for (String time : listWarningTimeText) {
            Calendar cal = convertTimeTextToCalendar(time);
            warningTimes.add(cal);
        }
        Collections.sort(warningTimes, Collections.reverseOrder());
        ConsoleUtils.printInfo(AutoRestartCore.NAME, "Sortierte Warnzeiten");
        for (Calendar cal : warningTimes) {
            ConsoleUtils.printInfo(AutoRestartCore.NAME, CheckThread.printCalendarTime(cal));
        }
    }

    public static List<Calendar> getRestartTimes() {
        return restartTimes;
    }

    public static List<Calendar> getWarningTimes() {
        return warningTimes;
    }
}

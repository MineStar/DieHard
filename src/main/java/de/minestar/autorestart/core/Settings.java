package de.minestar.autorestart.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.minestar.minestarlibrary.config.MinestarConfig;
import de.minestar.minestarlibrary.utils.ConsoleUtils;

public class Settings {

    /* VALUES */

    private static List<Long> restartTimes;
    private static List<Long> warningTimes;
    private static int lastWarning;

    /* USED FOR SETTING */

    private static MinestarConfig config;
    private static File configFile;

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
        /* Last warning time */
        loadLastWarning();
    }

    private static void loadRestartTimes() {
        List<String> listRestartTimeText = config.getStringList("Restart Times");

        restartTimes = new ArrayList<Long>();
        ConsoleUtils.printInfo(AutoRestartCore.NAME, "RestartTimes:");
        for (String time : listRestartTimeText) {
            ConsoleUtils.printInfo(AutoRestartCore.NAME, time);
            restartTimes.add(DateTimeHelper.getOnlyTimeLong(time));
        }
        Collections.sort(restartTimes);
    }

    private static void loadWarningTimes() {
        List<String> listWarningTimeText = config.getStringList("Warning Times");

        warningTimes = new ArrayList<Long>();
        ConsoleUtils.printInfo(AutoRestartCore.NAME, "WarningTimes:");
        for (String time : listWarningTimeText) {
            ConsoleUtils.printInfo(AutoRestartCore.NAME, time);
            warningTimes.add(DateTimeHelper.getOnlyTimeLong(time));
        }
        Collections.sort(warningTimes, Collections.reverseOrder());
    }

    private static void loadLastWarning() {
        lastWarning = config.getInt("Last Warning");
        ConsoleUtils.printInfo(AutoRestartCore.NAME, "LastWarning: " + lastWarning);
    }

    public static List<Long> getRestartTimes() {
        return restartTimes;
    }

    public static List<Long> getWarningTimes() {
        return warningTimes;
    }

    public static int getLastWarning() {
        return lastWarning;
    }
}

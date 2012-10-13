package de.minestar.diehard.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.minestar.minestarlibrary.config.MinestarConfig;
import de.minestar.minestarlibrary.utils.ConsoleUtils;

public class Settings {

    /* VALUES */

    private static List<Time> restartTimes;
    private static List<Time> warningTimes;
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

        restartTimes = new ArrayList<Time>();
        ConsoleUtils.printInfo(DieHardCore.NAME, "Restart Times:");
        for (String timeText : listRestartTimeText) {
            ConsoleUtils.printInfo(DieHardCore.NAME, timeText);
            Time time = new Time(timeText);
            restartTimes.add(time);
        }
        Collections.sort(restartTimes);
    }

    private static void loadWarningTimes() {
        List<String> listWarningTimeText = config.getStringList("Warning Times");

        warningTimes = new ArrayList<Time>();
        ConsoleUtils.printInfo(DieHardCore.NAME, "Warning Times:");
        for (String timeText : listWarningTimeText) {
            ConsoleUtils.printInfo(DieHardCore.NAME, timeText);
            Time time = new Time(timeText);
            warningTimes.add(time);
        }
        Collections.sort(warningTimes, Collections.reverseOrder());
    }

    private static void loadLastWarning() {
        lastWarning = config.getInt("Last Warning");
        ConsoleUtils.printInfo(DieHardCore.NAME, "LastWarning: " + lastWarning);
    }

    public static List<Time> getRestartTimes() {
        return restartTimes;
    }

    public static List<Time> getWarningTimes() {
        return warningTimes;
    }

    public static int getLastWarning() {
        return lastWarning;
    }
}

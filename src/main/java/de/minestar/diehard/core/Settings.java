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
        Settings.configFile = new File(dataFolder, "config.yml");
        try {
            // LOAD EXISTING CONFIG FILE
            if (Settings.configFile.exists()) {
                Settings.config = new MinestarConfig(Settings.configFile, pluginName, pluginVersion);
                // CREATE A DEFAUL ONE
            } else {
                Settings.config = MinestarConfig.copyDefault(Settings.class.getResourceAsStream("/config.yml"), Settings.configFile);
            }
            Settings.loadValues();
            return true;

        } catch (Exception e) {
            ConsoleUtils.printException(e, pluginName, "Can't load the settings from " + Settings.configFile);
            return false;
        }
    }

    private static void loadValues() {
        /* Restart times */
        Settings.loadRestartTimes();
        /* Warning times */
        Settings.loadWarningTimes();
        /* Last warning time */
        Settings.loadLastWarning();
    }

    private static void loadRestartTimes() {
        List<String> listRestartTimeText = Settings.config.getStringList("Restart Times");

        Settings.restartTimes = new ArrayList<Time>();
        ConsoleUtils.printInfo(DieHardCore.NAME, "Restart Times:");
        for (String timeText : listRestartTimeText) {
            ConsoleUtils.printInfo(DieHardCore.NAME, timeText);
            Time time = new Time(timeText);
            Settings.restartTimes.add(time);
        }
        Collections.sort(Settings.restartTimes);
    }

    private static void loadWarningTimes() {
        List<String> listWarningTimeText = Settings.config.getStringList("Warning Times");

        Settings.warningTimes = new ArrayList<Time>();
        ConsoleUtils.printInfo(DieHardCore.NAME, "Warning Times:");
        for (String timeText : listWarningTimeText) {
            ConsoleUtils.printInfo(DieHardCore.NAME, timeText);
            Time time = new Time(timeText);
            Settings.warningTimes.add(time);
        }
        Collections.sort(Settings.warningTimes, Collections.reverseOrder());
    }

    private static void loadLastWarning() {
        Settings.lastWarning = Settings.config.getInt("Last Warning");
        ConsoleUtils.printInfo(DieHardCore.NAME, "LastWarning: " + Settings.lastWarning);
    }

    public static List<Time> getRestartTimes() {
        return Settings.restartTimes;
    }

    public static List<Time> getWarningTimes() {
        return Settings.warningTimes;
    }

    public static int getLastWarning() {
        return Settings.lastWarning;
    }
}

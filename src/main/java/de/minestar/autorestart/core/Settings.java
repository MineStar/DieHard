package de.minestar.autorestart.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

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
		/* Restart Zeiten */
		loadRestartTimes();
	}
	
	private static Calendar convertTimeTextToCalendar(String timeText) {
		int hours = -1, minutes = -1;
		Calendar cal = new GregorianCalendar(0, 0, 0, 0, 0);
		String[] zeitfragmente = timeText.split(":");
		if (zeitfragmente.length == 2) {
			
			try {
				hours = Integer.parseInt(zeitfragmente[0]);
				minutes = Integer.parseInt(zeitfragmente[1]);
			} catch (NumberFormatException e) {
				// Zeitwert enthielt ung�ltige Zeichen 
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
		List<String> listWarningTimeText = config.getStringList("Warning Times");
		
		restartTimes = new ArrayList<Calendar>();
		for (String zeit : listRestartTimeText) {
			Calendar cal = convertTimeTextToCalendar(zeit);
			restartTimes.add(cal);
		}
		Collections.sort(restartTimes);
		
		warningTimes = new ArrayList<Calendar>();
		for (String zeit : listWarningTimeText) {
			Calendar cal = convertTimeTextToCalendar(zeit);
			warningTimes.add(cal);
		}
		Collections.sort(restartTimes);
	}

	public static List<Calendar> getRestartTimes() {
		return restartTimes;
	}
	
	public static List<Calendar> getWarningTimes() {
		return warningTimes;
	}
}
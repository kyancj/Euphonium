package net.cancheta.settings;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import net.cancheta.ai.path.world.BlockSet;
import net.cancheta.settings.serialize.BlockSetAdapter;
import net.minecraft.client.MinecraftClient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;

public class EuphoniumSettings {
	private static final Marker MARKER_SETTINGS = MarkerManager
			.getMarker("settings");
	private static final Logger LOGGER = LogManager.getLogger(EuphoniumSettings.class);
	
	private static final EuphoniumSettings INSTANCE = new EuphoniumSettings();

	private EuphoniumSettingsRoot settings;
	private ArrayList<String> keys;

	private long settingsLastModified = 0;

	private static Object mutex = new Object();

	private EuphoniumSettings() {
	}

	private synchronized EuphoniumSettingsRoot createSettings() {
		File settingsFile = getSettingsFile();
		if (settings == null || changedSinceLastLoad(settingsFile)) {
			settings = null;
			try {
				settingsLastModified = settingsFile.lastModified();
				LOGGER.debug(MARKER_SETTINGS, "Loading " + settingsFile.getAbsolutePath()
						+ " ... (date: " + new Date(settingsLastModified) + ")");
				Gson gson = getGson();
				settings = gson.fromJson(new FileReader(settingsFile),
						EuphoniumSettingsRoot.class);
				validateAfterLoad(settings);
			} catch (final IOException e) {

				LOGGER.error(MARKER_SETTINGS, "Could not read settings file: " + e.getMessage());
			} catch (final JsonParseException e) {
				LOGGER.error(MARKER_SETTINGS, "Error in settings file:" + e.getMessage());
			}
			if (settings == null) {
				LOGGER.info(MARKER_SETTINGS, "Fall back to default settings.");
				settings = new EuphoniumSettingsRoot();
			}
		}

		return settings;
	}

	private boolean changedSinceLastLoad(File settingsFile) {
		return settingsFile.lastModified() > settingsLastModified;
	}

	private void doWriteSettings() {
		EuphoniumSettingsRoot settings = createSettings();

		File settingsFile = getSettingsFile();
		try {
			LOGGER.trace(MARKER_SETTINGS, "Writing " + settingsFile.getAbsolutePath()
					+ " ...");
			Gson gson = getGson();
			PrintWriter writer = new PrintWriter(settingsFile);
			gson.toJson(settings, writer);
			writer.close();
		} catch (final IOException e) {
			System.err.println("Could not write settings file.");
		}
	}

	public static Gson getGson() {
		GsonBuilder gson = new GsonBuilder();
		gson.setPrettyPrinting();
		gson.registerTypeAdapter(BlockSet.class, new BlockSetAdapter());
		return gson.create();
	}

	private void validateAfterLoad(EuphoniumSettingsRoot loaded) {
		FieldValidation.validateAfterLoad(loaded, new EuphoniumSettingsRoot());
	}

	public static EuphoniumSettingsRoot getSettings() {
		return getInstance().createSettings();
	}

	public static void writeSettings() {
		INSTANCE.doWriteSettings();
	}

	public static File getDataDir() {
		File dir = new File(MinecraftClient.getInstance().runDirectory, "minebot");
		LOGGER.trace(MARKER_SETTINGS, "Data directory: " + dir);
		if (false && !dir.isDirectory()) {
			try {
				System.out.println("CREATING NEW DIR");
				return new EuphoniumDirectoryCreator().createDirectory(dir);
			} catch (IOException e) {
				LOGGER.error(MARKER_SETTINGS, "Could not create settings directory.");
				e.printStackTrace();
			}
		}

		return dir;
	}

	public static File getDataDirFile(String name) {

		return new File(getDataDir(), name);
	}

	public static File getSettingsFile() {
		return getDataDirFile("minebot.json");
	}

	private static EuphoniumSettings getInstance() {
		return INSTANCE;
	}
}

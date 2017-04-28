package persistance;

import java.io.File;

public class Settings extends Savable {
	private transient static Settings ourInstance = new Settings("settings.bin");
	// default directory for saving settings etc
	private transient static final File defaultDirectory = new File(System.getProperty("user.home") + "/.CompaRate");
	// user-set save directory
	private File customDirectory;

	private Settings(final String saveFileName) {
		super(saveFileName);
	}

	public static File getDefaultDirectory() {
		return defaultDirectory;
	}

	public static Settings getInstance() {
		return ourInstance;
	}

	public File getCustomDirectory() {
		return customDirectory;
	}

	public void setCustomDirectory(final File customDirectory) {
		this.customDirectory = customDirectory;
	}

	// load the settings object from disk and replace the current object if loading was successful
	@Override
	public Savable load(final File directory, final String saveFileName) {
		Settings loaded = (Settings) super.load(directory, saveFileName);
		if (loaded != null) {
			ourInstance = loaded;
		}
		return ourInstance;
	}
}

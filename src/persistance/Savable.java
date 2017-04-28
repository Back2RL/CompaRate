package persistance;

import java.io.*;

public class Savable implements Serializable {

	private static final long serialVersionUID = 1L;
	private String saveFileName = "save.bin";

	public Savable(final String saveFileName) {
		this.saveFileName = saveFileName;
	}

	public Savable load(final File directory, final String saveFileName) {
		Savable result = null;
		File saveFile = new File(directory.getAbsolutePath() + "/" + saveFileName);
		try (FileInputStream fileInputStream = new FileInputStream(saveFile);
			 ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
			result = (Savable) objectInputStream.readObject();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return result;
	}

	public String getSaveFileName() {
		return saveFileName;
	}

	public void setSaveFileName(final String saveFileName) {
		this.saveFileName = saveFileName;
	}

	// save Object to User-Home-Directory
	public void save() {
		save(Settings.getDefaultDirectory());
	}

	// save Object to User-Specified-Directory
	public void save(final File directory) {
		// TODO: error handling
		if (!directory.exists()) {
			try {
				directory.mkdir();
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		} else if (!directory.isDirectory()) {
			throw new IllegalArgumentException("path must point to a directory, not a file!");
		}

		final File saveFile = new File(directory.getAbsolutePath() + "/" + saveFileName);
		try (FileOutputStream fileOutputStream = new FileOutputStream(saveFile);
			 ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
			objectOutputStream.writeObject(this);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

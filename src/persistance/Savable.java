package persistance;

import java.io.*;

public class Savable implements Serializable {

	private transient final static String path = System.getProperty("user.home") + "/.CompaRate";
	private transient static File directory = new File(path);
	private transient static File saveFile = new File(path + "/save.object");

	private static final long serialVersionUID = 1L;

	// save Object to User-Home-Directory
	public void save() {
		if (!directory.exists()) {
			try {
				directory.mkdir();
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}

		try (FileOutputStream fileOutputStream = new FileOutputStream(saveFile);
			 ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
			objectOutputStream.writeObject(this);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// read Object from User-Home-Directory
	public static Savable load() {
		Savable result = null;
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
}

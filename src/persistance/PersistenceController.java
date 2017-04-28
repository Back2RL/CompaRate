package persistance;


import java.io.File;

public class PersistenceController {
	private static PersistenceController ourInstance = new PersistenceController();

	public static PersistenceController getInstance() {
		return ourInstance;
	}

	private PersistenceController() {
	}

	public static void main(String[] args){

		Settings.getInstance().save();
		System.out.println(Settings.getInstance().getCustomDirectory());
		Settings.getInstance().setCustomDirectory(new File("/"));
		System.out.println(Settings.getInstance().getCustomDirectory());
		Settings.getInstance().save();

		Settings.getInstance().setCustomDirectory(new File("/Hans"));
		System.out.println(Settings.getInstance().getCustomDirectory());

		Settings.getInstance().load(Settings.getDefaultDirectory(),Settings.getInstance().getSaveFileName());
		System.out.println(Settings.getInstance().getCustomDirectory());



	}


}

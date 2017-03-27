package persistance;


public class PersistanceController {
	private static PersistanceController ourInstance = new PersistanceController();

	public static PersistanceController getInstance() {
		return ourInstance;
	}

	private PersistanceController() {
	}
}

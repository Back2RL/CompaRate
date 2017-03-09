package logic;

import java.io.File;

/**
 * Created by leonard on 22.01.17.
 */
public class Controller{
	private static Controller ourInstance = new Controller();

	public static Controller getInstance() {
		return ourInstance;
	}

	private Controller() {
	}

	public boolean startAnalyzingDirectory(final File directory) throws Exception {
		return AnalysisManager.getInstance().analyzeDir(directory);
	}

	public static int getFileCount(){
		return AnalysisManager.getInstance().getFiles().size();
	}

	public static void main(String args[]){
		try {
			Controller.testController();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void testController(){
		try {
			Controller.getInstance().startAnalyzingDirectory(new File("/home"));
		} catch(Exception e){

		}
		AnalysisManager.getInstance().pauseAnalysis();
		System.out.println(AnalysisManager.getInstance().continueAnalysis());
		try {
			Controller.getInstance().startAnalyzingDirectory(new File("/home"));
		} catch(Exception e){
			AnalysisManager.getInstance().abortAnalyzing();
			try {
				Controller.getInstance().startAnalyzingDirectory(new File("/home"));
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		while(AnalysisManager.getInstance().isAnalyzing()){
			try {
				Thread.sleep(500		);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println(getFileCount()+" files found; has finished: "+AnalysisManager.getInstance().hasFinished());
		}

		System.out.println(getFileCount()+" files found;  has finished: "+AnalysisManager.getInstance().hasFinished());
	}
}

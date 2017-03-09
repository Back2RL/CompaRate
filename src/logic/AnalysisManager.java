package logic;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by leonard on 22.01.17.
 */
public class AnalysisManager {
	private AnalysisWorker analysisWorker;

	private static AnalysisManager ourInstance = new AnalysisManager();

	public static AnalysisManager getInstance() {
		return ourInstance;
	}

	private AnalysisManager() {
	}

	/**
	 * @param directory
	 * @return
	 * @throws Exception if there is currently an analysis ongoing
	 */
	public boolean analyzeDir(final File directory) throws Exception {
		if (isAnalyzing()) {
			throw new Exception("AnalysisManager: Can not start new analysis. AnalysisManager is still running. Abort it to start a new Task.");
		}
		analysisWorker = new AnalysisWorker(directory);
		analysisWorker.setThread(new Thread(analysisWorker));
		analysisWorker.getThread().start();
		return isAnalyzing();
	}

	public boolean isAnalyzing() {
		return analysisWorker != null && (analysisWorker.isAnalyzing());
	}

	public void abortAnalyzing() {
		if (isAnalyzing()) {
			analysisWorker.abort();
		}
		while(analysisWorker.isAnalyzing()){
			System.out.println("Aborting Analysis");
			try {
				Thread.sleep(10L);
			} catch (InterruptedException e) {
			}
		}
	}

	public void pauseAnalysis() {
		if (isAnalyzing()) {
			analysisWorker.pause();
		}
	}

	public boolean continueAnalysis() {
		return analysisWorker != null && analysisWorker.isAnalyzing() && analysisWorker.continueAnalysis();
	}

	public List<File> getFiles() {
		return analysisWorker != null ? analysisWorker.getFiles() : new ArrayList<>();
	}

	public boolean hasFinished(){
		return analysisWorker != null ? analysisWorker.hasAnalysisSucceeded() : false;
	}


}

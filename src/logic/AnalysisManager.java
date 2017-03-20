package logic;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnalysisManager {
	private Map<File, AnalysisWorker> analysisJobs;

	private static AnalysisManager ourInstance = new AnalysisManager();

	public static AnalysisManager getInstance() {
		return ourInstance;
	}

	private AnalysisManager() {
		analysisJobs = new HashMap<>();
	}

	/**
	 * @param directory directory that shall be analyized
	 * @return false if a AnalysisManager for this directory is still running. Abort it to start a new Task for this directory.
	 * @throws IllegalArgumentException if the directory is invalid
	 */
	public boolean analyzeDir(final File directory) throws IllegalArgumentException {
		checkErrors(directory);
		if (analysisJobs.containsKey(directory)) {
			return false;
		}
		AnalysisWorker analysisWorker = new AnalysisWorker(directory);
		analysisWorker.setThread(new Thread(analysisWorker));
		analysisWorker.getThread().start();
		analysisJobs.put(directory, analysisWorker);
		return isAnalyzing(directory);
	}

	public boolean isAnalyzing(final File directory) throws IllegalArgumentException {
		checkErrors(directory);
		AnalysisWorker worker = analysisJobs.get(directory);
		return worker != null && (worker.isAnalyzing());
	}

	public void abortAnalyzing(final File directory) throws IllegalArgumentException {
		checkErrors(directory);
		AnalysisWorker analysisWorker = analysisJobs.get(directory);
		if(analysisWorker != null) {
			analysisWorker.abort();
			while (analysisWorker.isAnalyzing()) {
				System.out.println("Aborting Analysis");
				try {
					Thread.sleep(10L);
				} catch (InterruptedException e) {
					System.err.println("A worker is still pending abort!");
				}
			}
			analysisJobs.remove(directory);
		}
	}

	public void pauseAnalysis(final File directory) throws IllegalArgumentException {
		checkErrors(directory);
		if (isAnalyzing(directory)) {
			AnalysisWorker analysisWorker = analysisJobs.get(directory);
			analysisWorker.pause();
		}
	}

	public boolean continueAnalysis(final File directory) throws IllegalArgumentException {
		checkErrors(directory);
		AnalysisWorker analysisWorker = analysisJobs.get(directory);
		return analysisWorker != null && analysisWorker.isAnalyzing() && analysisWorker.continueAnalysis();
	}

	public List<File> getFiles(final File directory) throws IllegalArgumentException {
		checkErrors(directory);
		AnalysisWorker analysisWorker = analysisJobs.get(directory);
		List<File> files = null;
		if (analysisWorker != null) {
			files = analysisWorker.getFiles();
		} else {
			System.err.println("No worker exists for \"" + directory.getAbsolutePath() + "\"");
		}
		return files;
	}

	public boolean hasFinished(final File directory) throws IllegalArgumentException {
		checkErrors(directory);
		AnalysisWorker analysisWorker = analysisJobs.get(directory);
		return analysisWorker != null ? analysisWorker.hasAnalysisSucceeded() : false;
	}

	private void checkErrors(final File directory) throws IllegalArgumentException {
		if (directory == null || !directory.exists() || !directory.isDirectory() || !directory.canRead()) {
			throw new IllegalArgumentException("directory is not valid (null/non-existent/not a directory/no read access)");
		}
	}
}

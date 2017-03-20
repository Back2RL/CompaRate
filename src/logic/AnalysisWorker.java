package logic;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class AnalysisWorker implements Runnable {
	final List<File> directories;

	public List<File> getFiles() {
		return files;
	}

	final List<File> files;
	private volatile boolean abortRequested = false;
	private volatile boolean pauseRequested = false;
	private volatile boolean stopRequested = false;
	private volatile boolean analysisSucceeded = false;
	private volatile boolean isPaused = false;

	private long startTime;
	private long endTime = 0;
	private long pauseStartTime;
	private long pausedTime = 0;

	public float getAnalysisTime(){
		if(isPaused){
			return (pauseStartTime - startTime - pausedTime) * 1E-3f;
		}
		if(endTime == 0L){
			return (System.currentTimeMillis() - startTime - pausedTime) * 1E-3f;
		}
		return (endTime - startTime - pausedTime) * 1E-3f;
	}

	public boolean hasAnalysisSucceeded() {
		return analysisSucceeded;
	}

	private volatile Thread thread;

	public AnalysisWorker(final File startDirectory) {
		startTime = System.currentTimeMillis();
		directories = new ArrayList<>();
		directories.add(startDirectory);
		files = new ArrayList<>();
	}

	public synchronized Thread getThread() {
		return thread;
	}

	public void setThread(final Thread thread) {
		this.thread = thread;
	}

	@Override
	public void run() {
		File analysisDir = directories.get(0);
		if (analysisDir == null) {
			try {
				throw new IOException("Analysis-directory is null!");
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}
		if (!analysisDir.isDirectory()) {
			try {
				throw new IOException("Analysis-directory is not a directory!");
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}
		if (!analysisDir.canRead()) {
			try {
				throw new IOException("Reading from analysis-directory failed!");
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}

		List<File> pendingDirectories = new ArrayList<>();

		while (!stopRequested) {
			try {
				for (File currentFile : analysisDir.listFiles()) {
					checkPause();
					if (abortRequested || stopRequested) {
						break;
					}
					if (Files.isSymbolicLink(currentFile.toPath())) {
						continue;
					}
					if (currentFile.isDirectory()) {
						pendingDirectories.add(currentFile);
						continue;
					}
					if (currentFile.isFile()) {
						files.add(currentFile);
					} else {
						System.err.println("Not a file: " + currentFile);
					}
				}
			} catch (NullPointerException e) {
				if (analysisDir != null)
					System.err.println("Can not access: " + analysisDir.toString());
			}
			if (!pendingDirectories.isEmpty()) {
				analysisDir = pendingDirectories.get(0);
				pendingDirectories.remove(0);
			} else {
				break;
			}

			checkPause();
			if (abortRequested) {
				System.out.println("AnalysisWorker: aborting...");
				directories.clear();
				files.clear();
				return;
			}
		}
		if (!stopRequested) {
			analysisSucceeded = true;
		}
		endTime = System.currentTimeMillis();
		System.out.println("-----");
		System.out.println("Number of found files: " + files.size());
		System.out.println("Runtime = "+getAnalysisTime()+" seconds.");

	}

	private void checkPause() {
		if (pauseRequested) {
			isPaused = true;
			pauseStartTime = System.currentTimeMillis();
			while (pauseRequested && !(abortRequested || stopRequested)) {
				try {
					System.out.println("AnalysisWorker: pausing...");
					thread.sleep(10_000L);
				} catch (InterruptedException e) {
				}
			}
			isPaused = false;
			pausedTime += System.currentTimeMillis() - pauseStartTime;
			System.out.println("AnalysisWorker: continuing after " + pausedTime * 1E-3f + " seconds...");
		}
	}

	public synchronized void abort() {
		abortRequested = true;
	}

	public synchronized void pause() {
		pauseRequested = true;
	}

	public synchronized boolean continueAnalysis() {
		if (thread.isAlive() && pauseRequested) {
			pauseRequested = false;
			if (isPaused) {
				thread.interrupt();
			}
			return true;
		}
		return false;
	}

	public synchronized boolean isAnalyzing() {
		//System.out.println(thread.getName() + " is asked whether it is analyzing...");
		return thread.isAlive();// && !thread.isInterrupted();
	}

	private File getDirectory(String path) {
		// invariant 1
		if (path == null || path.equals("")) {
			throw new IllegalArgumentException("Given path must not be empty");
		}
		// invariant 2
		File file = null;
		try {
			file = new File(path);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!file.isDirectory()) {
			throw new IllegalArgumentException("Given path does not point to a directory");
		}
		return file;
	}
}

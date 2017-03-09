package logic;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by leonard on 22.01.17.
 */
public class AnalysisWorker implements Runnable {
	final List<File> directories;

	public List<File> getFiles() {
		return files;
	}

	final List<File> files;
	volatile boolean abortRequested = false;
	volatile boolean pauseRequested = false;
	volatile boolean stopRequested = false;
	volatile boolean analysisSucceeded = false;

	public boolean hasAnalysisSucceeded() {
		return analysisSucceeded;
	}

	private volatile Thread thread;

	public AnalysisWorker(final File startDirectory) {
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
		// System.out.println(files.toString());
		System.out.println("-----");
		System.out.println("Number of found files: " + files.size());

	}

	private void checkPause() {
		if (pauseRequested) {
			while (pauseRequested && !(abortRequested || stopRequested)) {
				try {
					System.out.println("AnalysisWorker: pausing...");
					thread.sleep(10_000L);
				} catch (InterruptedException e) {
				}
			}
			System.out.println("AnalysisWorker: continuing...");
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
			thread.interrupt();
			return true;
		}
		return false;
	}

	public synchronized boolean isAnalyzing() {
		System.out.println(thread.getName() + " is asked whether it is analyzing...");
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

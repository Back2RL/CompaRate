package logic;


import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.util.*;

public class IndexManager {
	private static final String indexSeperator = ";";
	private final boolean DEBUG = true;
	private boolean bUseMaxFileSize;
	private long maxFileSize = 100 * 1024; // 100 KB

	private final String indexFileName = "index.csv";
	private String indexDirectoryPath;

	private Map<String, TreeSet<File>> indexFromFile = new TreeMap<String, TreeSet<File>>();
	private File index;
	private File indexDirectory;

	public boolean isbUseMaxFileSize() {
		return bUseMaxFileSize;
	}

	public void setbUseMaxFileSize(boolean bUseMaxFileSize) {
		this.bUseMaxFileSize = bUseMaxFileSize;
	}

	public long getMaxFileSize() {
		return maxFileSize;
	}

	public void setMaxFileSize(long maxFileSize) {
		this.maxFileSize = maxFileSize;
	}

	/**
	 * @param pathToIndexDirectory
	 * @param maxFileSize
	 *            in Bytes 100 * 1024 => 100KB
	 * @throws IllegalArgumentException
	 *             given path has to point to a directory and must not be
	 *             empty/null
	 */
	IndexManager(String pathToIndexDirectory, long maxFileSize) {
		this.bUseMaxFileSize = true;
		this.maxFileSize = maxFileSize;
		indexDirectory = getDirectory(pathToIndexDirectory);
		indexDirectoryPath = getDirectory(pathToIndexDirectory).getAbsolutePath() + File.separatorChar;
	}

	IndexManager( long maxFileSize) {
		String pathToIndexDirectory = System.getProperty("user.home");
		if(!(new File(pathToIndexDirectory).exists() && new File(pathToIndexDirectory).isDirectory())){
			File dir = new File(pathToIndexDirectory);
			//dir.createNewFile();
		}
		this.bUseMaxFileSize = true;
		this.maxFileSize = maxFileSize;
		indexDirectory = getDirectory(pathToIndexDirectory);
		indexDirectoryPath = getDirectory(pathToIndexDirectory).getAbsolutePath() + File.separatorChar;
	}

	/**
	 * @param pathToIndexDirectory
	 * @throws IllegalArgumentException
	 *             given path has to point to a directory and must not be
	 *             empty/null
	 */
	IndexManager(String pathToIndexDirectory) {
		this.bUseMaxFileSize = false;
		indexDirectory = getDirectory(pathToIndexDirectory);
		indexDirectoryPath = getDirectory(pathToIndexDirectory).getAbsolutePath() + File.separatorChar;
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

	/**
	 * attempts to load the index file
	 * 
	 * @return true when a file exists
	 */
	boolean loadIndex() {
		String indexPath = indexDirectoryPath + indexFileName;
		System.out.println("Looking for file: \"" + indexPath + "\"");
		index = new File(indexPath);
		if (index.exists()) {
			scanIndex();
			return true;
		} else {
			return false;
		}
	}

	/**
	 * creates a backup of the index file, modified with a timestamp
	 */
	void createIndexBackup() {
		int dotIndex = indexFileName.lastIndexOf(".");
		String fileName = null;
		String fileExt = "";
		if (dotIndex == -1) {
			fileName = indexFileName;
		} else {
			fileName = indexFileName.substring(0, dotIndex);
			fileExt = indexFileName.substring(dotIndex);
		}

		try {
			Files.copy(index.toPath(),
					new File(indexDirectoryPath + fileName + "_" + Time.timeNowToString() + fileExt).toPath());
		} catch (FileAlreadyExistsException e) {
			System.out.println("There is already a valid backup file");
		} catch (IOException e) {
			System.out.println("No backup was created! EXIT");
			e.printStackTrace();
			System.exit(0);
		}

		if (DEBUG) {
			System.out.println("Listing all files in directory: \"" + indexDirectoryPath + "\"");
			System.out.println("-----");
			for (File file : indexDirectory.listFiles()) {
				System.out.println(file.getName());
			}
		}
		System.out.println("-----");
	}

	/**
	 * load content of indexfile into memory
	 */
	private void scanIndex() {

		try (BufferedReader br = new BufferedReader(new FileReader(index))) {

			int lineCnt = 0;
			int notFoundFiles = 0;
			System.out.println("-----");

			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				++lineCnt;

				String[] lineContent = sCurrentLine.split(indexSeperator);
				if (lineContent.length < 2) {
					System.out.println("No valid index in line: " + lineCnt);
					continue;
				}
				if (DEBUG) {
					System.out.println(Arrays.toString(lineContent));
				}

				TreeSet<File> entries = new TreeSet<>();
				for (int i = 1; i < lineContent.length; ++i) {

					File file = new File(lineContent[i]);
					if (file.exists()) {
						entries.add(file);
					} else {
						++notFoundFiles;
						System.out.println("Missing: " + lineContent[i]);
					}
				}

				if (entries.isEmpty())
					continue;
				indexFromFile.put(lineContent[0], entries);
			}
			System.out.println("-----");
			System.out.println("Indexfile loaded.");
			if (notFoundFiles > 0) {
				System.out.println(notFoundFiles + " files no longer exist.");
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void printIndex() {
		for (String key : indexFromFile.keySet()) {
			int cnt = 0;
			for (File file : indexFromFile.get(key)) {
				++cnt;
				System.out.println(key + ": " + cnt + ". " + file.getAbsolutePath());
			}
		}
		System.out.println();
	}

	public void analyzeDirectory(String dir) {
		File analysisDir = getDirectory(dir);
		System.out.println("Starting analysis of: \"" + analysisDir + "\"");

		List<File> pendingDirectories = new ArrayList<>();
		List<File> allFiles = new ArrayList<>();

		while (true) {
			try {
				for (File currentFile : analysisDir.listFiles()) {
					if (bUseMaxFileSize && currentFile.length() > maxFileSize) {
						System.out.println("Ignored: " + analysisDir.toString());
						continue;
					}
					if (Files.isSymbolicLink(currentFile.toPath())) {
						continue;
					}
					if (currentFile.isDirectory()) {
						pendingDirectories.add(currentFile);
						continue;
					}

					allFiles.add(currentFile);
				}
			} catch (NullPointerException e) {
				System.out.println("Can not access: " + analysisDir.toString() + " : NullPointerException");
			}
			if (!pendingDirectories.isEmpty()) {
				analysisDir = pendingDirectories.get(0);
				pendingDirectories.remove(0);
			} else {
				break;
			}
		}
		// System.out.println(files.toString());
		System.out.println("-----");
		System.out.println("Number of found files: " + allFiles.size());
		merge(allFiles);
	}

	public void saveIndex() {
		System.out.println("Saving new index to disk...");
		try (BufferedWriter writer = new BufferedWriter(new PrintWriter(indexDirectoryPath + indexFileName, "UTF-8"))) {
			for (String key : indexFromFile.keySet()) {
				writer.write(key);
				if (indexFromFile.get(key) != null) {
					for (File file : indexFromFile.get(key)) {
						writer.write(indexSeperator + file.toString());
					}
				}
				writer.newLine();
			}
			writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("New index has been written");
	}

	public void merge(List<File> allFiles) {
		System.out.println("Merging result with existing Index...");
		int alreadyIndexed = 0;
		for (File file : allFiles) {

			try {
				// get the hash of the current file
				String hash = MD5.fromFile(file);
				if (hash != null) {
					// search the index for the hash
					if (indexFromFile.containsKey(hash)) {
						//
						if (indexFromFile.get(hash) != null && indexFromFile.get(hash).contains(file)) {
							++alreadyIndexed;
							continue;
						}
						try {
							indexFromFile.get(hash).add(file);
						} catch (NullPointerException e) {
							TreeSet<File> entries = new TreeSet<>();
							entries.add(file);
							indexFromFile.put(hash, entries);
						}
					} else {
						TreeSet<File> entries = new TreeSet<>();
						entries.add(file);
						indexFromFile.put(hash, entries);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (alreadyIndexed > 0) {
			System.out.println(alreadyIndexed + " files already indexed.");
		}
	}
}

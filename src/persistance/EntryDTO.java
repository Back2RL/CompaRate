package persistance;

import java.io.File;
import java.io.Serializable;
import java.util.HashSet;

/**
 * Created by leonard on 27.03.17.
 */
public class EntryDTO implements Serializable{
	private File file;
	private String checkSum;
	private long dateTimeRated;
	private long dateTimeLastAccessed;
	private long accessCount;
	private HashSet<Tag> tags;

	public EntryDTO(final File file, final String checkSum, final long dateTimeRated, final long dateTimeLastAccessed) {
		this.file = file;
		this.checkSum = checkSum;
		this.dateTimeRated = dateTimeRated;
		this.dateTimeLastAccessed = dateTimeLastAccessed;
	}
}

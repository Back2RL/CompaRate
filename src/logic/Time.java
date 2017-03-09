package logic;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Time {
	/**
	 * @return a String containing a timestamp with current date and time,
	 *         format: year-month-dayThour-minute-second-millisecond
	 */
	public static String timeNowToString() {
		LocalDateTime date = LocalDateTime.now();
		String timeString = date.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
		// LocalDateTime parsedDate = LocalDateTime.parse(timeString,
		// DateTimeFormatter.ISO_LOCAL_DATE_TIME);
		timeString = timeString.replaceAll(":", "-");
		timeString = timeString.replaceAll("\\.", "-");
		return timeString;
	}
}

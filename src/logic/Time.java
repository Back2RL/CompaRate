package logic;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Time {

	// pattern: yearmonthdayhourminutesecondmillisecond
	public static final DateTimeFormatter UTC_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddhhmmssSSS");

	/**
	 * @return a String containing a timestamp with current date and time,
	 * format: utcyearmonthdayhourminutesecondmillisecond
	 */
	public static String get_UTC_now() {
		// get the current utc-time
		final ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);
		// format it into a string that can be used in filenames
		final String timeString = "utc" + utc.format(UTC_FORMATTER);
		return timeString;
	}
}

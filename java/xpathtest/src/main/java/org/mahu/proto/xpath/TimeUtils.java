package org.mahu.proto.xpath;

import java.time.Duration;
import java.time.Instant;

final class TimeUtils {
	
	private static final String ZERO_TIMESTAMP = "0000-00-00T00:00:00.000000Z";

	static Instant convertTimeStampString(final String timestamp) {
		if (timestamp.equals(ZERO_TIMESTAMP)) {
			return Instant.MIN;
		}

		return Instant.parse(timestamp);
	}	
	
	static Duration calculateDuration(final String startTimeString, final String endTimeString) {
		Instant startTime = TimeUtils.convertTimeStampString(startTimeString);
		Instant endTime = TimeUtils.convertTimeStampString(endTimeString);
		Duration between = Duration.between(startTime, endTime);
		return between;
	}
	
	static long calculateDurationInSecondsRounded(Duration between) {
		return (long) Math.round((between.toMillis() + 500) / 1000);
	}	

}

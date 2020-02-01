package org.mahu.proto.jerseystreaming;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public final class UTCTime {
    /**
     * Return UTC time encoded as ISO 8601
     */
    private static final String DATEFORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'000Z'";
    private static final String DATEFORMAT_SHORT_NOTATION = "yyyyMMdd'T'HHmmss'Z'";

    public static String getUTCdatetimeAsString() {
        final SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        final String utcTime = sdf.format(new Date());
        return utcTime;
    }

    public static String getUTCdatetimeShortNotationAsString() {
        final SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMAT_SHORT_NOTATION);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        final String utcTime = sdf.format(new Date());
        return utcTime;
    }

    public static String convertToConstraintedFormat(final String datetime) {
        final String tmp = datetime.replaceAll("\\-", "").replaceAll("\\:", "");
        return tmp.substring(0, 15) + 'Z';
    }

    private UTCTime() {
        // empty
    }
}
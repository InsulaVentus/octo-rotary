package services;

public class TimestampConverter {
    public static String toHoursFromEpoch(final String fromMillisFromEpoch) {
        return String.format("%d", Long.parseLong(fromMillisFromEpoch) / 3600000);
    }
}

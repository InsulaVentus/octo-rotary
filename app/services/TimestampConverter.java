package services;

class TimestampConverter {
    static String toHoursFromEpoch(final String fromMillisFromEpoch) {
        return String.format("%d", Long.parseLong(fromMillisFromEpoch) / 3600000);
    }
}

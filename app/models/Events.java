package models;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Events implements Serializable {
    private final Set<String> userIds = new HashSet<>();
    private int clicks = 0;
    private int impressions = 0;

    public Events(final String userId, final String event) {
        add(userId, event);
    }

    public void add(final String userId, final String event) {
        if ("click".equals(event)) {
            clicks++;
        } else {
            impressions++;
        }
        userIds.add(userId);
    }

    @Override
    public String toString() {
        return String.format("unique_users,%d\nclicks,%d\nimpressions,%d\n", userIds.size(), clicks, impressions);
    }
}

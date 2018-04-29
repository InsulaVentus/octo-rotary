package models;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Events implements Serializable {
    private final Set<String> userIds = new HashSet<>();
    private int clicks = 0;
    private int impressions = 0;

    public Events(final String userId, final String event) {
        addEvent(userId, event);
    }

    public void addEvent(final String userId, final String event) {
        if ("click".equals(event)) {
            clicks++;
        } else {
            impressions++;
        }
        userIds.add(userId);
    }

    public int getUniqueUsers() {
        return userIds.size();
    }

    public int getClicks() {
        return clicks;
    }

    public int getImpressions() {
        return impressions;
    }
}

package io.aloketewary.bloodconnect.fragment.pages;

/**
 * Created by AlokeT on 2/5/2018.
 */

public class Messages {

    private String message, type, from;
    private long timestamp;
    private boolean seen;

    public Messages() {
    }

    public Messages(String message, String type, String from, long timestamp, boolean seen) {
        this.message = message;
        this.type = type;
        this.from = from;
        this.timestamp = timestamp;
        this.seen = seen;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}

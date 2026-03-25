package au.com.sportsbet.movietickets.model.response;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TicketType {
    ADULT("Adult"),
    SENIOR("Senior"),
    TEEN("Teen"),
    CHILDREN("Children");

    private final String displayName;

    TicketType(String displayName) {
        this.displayName = displayName;
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }
}

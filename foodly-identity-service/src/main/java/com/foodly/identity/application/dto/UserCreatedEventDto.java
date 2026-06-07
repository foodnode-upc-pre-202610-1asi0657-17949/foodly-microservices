package com.foodly.identity.application.dto;

import java.time.Instant;
import java.util.List;

public class UserCreatedEventDto {

    private String eventId;
    private String eventType;
    private Instant occurredAt;
    private String sourceService = "foodly-identity-service";

    private String       userId;
    private String       email;
    private String       username;
    private String       firstName;
    private String       lastName;
    private String       phoneNumber;
    private List<String> roles;
    private boolean      active;

    public UserCreatedEventDto() {}

    public UserCreatedEventDto(String eventId,
                               String eventType,
                               Instant occurredAt,
                               String userId,
                               String email,
                               String username,
                               String firstName,
                               String lastName,
                               String phoneNumber,
                               List<String> roles,
                               boolean active) {
        this.eventId       = eventId;
        this.eventType     = eventType;
        this.occurredAt    = occurredAt;
        this.userId        = userId;
        this.email         = email;
        this.username      = username;
        this.firstName     = firstName;
        this.lastName      = lastName;
        this.phoneNumber   = phoneNumber;
        this.roles         = roles;
        this.active        = active;
    }

    public String getEventId()                         { return eventId; }
    public void setEventId(String eventId)             { this.eventId = eventId; }

    public String getEventType()                       { return eventType; }
    public void setEventType(String eventType)         { this.eventType = eventType; }

    public Instant getOccurredAt()                     { return occurredAt; }
    public void setOccurredAt(Instant occurredAt)      { this.occurredAt = occurredAt; }

    public String getSourceService()                   { return sourceService; }
    public void setSourceService(String sourceService) { this.sourceService = sourceService; }

    public String getUserId()                          { return userId; }
    public void setUserId(String userId)               { this.userId = userId; }

    public String getEmail()                           { return email; }
    public void setEmail(String email)                 { this.email = email; }

    public String getUsername()                        { return username; }
    public void setUsername(String username)           { this.username = username; }

    public String getFirstName()                       { return firstName; }
    public void setFirstName(String firstName)         { this.firstName = firstName; }

    public String getLastName()                        { return lastName; }
    public void setLastName(String lastName)           { this.lastName = lastName; }

    public String getPhoneNumber()                     { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber)     { this.phoneNumber = phoneNumber; }

    public List<String> getRoles()                     { return roles; }
    public void setRoles(List<String> roles)           { this.roles = roles; }

    public boolean isActive()                          { return active; }
    public void setActive(boolean active)              { this.active = active; }
}

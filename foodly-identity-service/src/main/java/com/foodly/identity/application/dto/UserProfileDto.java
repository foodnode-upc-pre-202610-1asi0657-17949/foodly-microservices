package com.foodly.identity.application.dto;

import java.util.List;

public class UserProfileDto {

    private String       userId;
    private String       email;
    private String       username;
    private String       firstName;
    private String       lastName;
    private String       phoneNumber;
    private List<String> roles;
    private boolean      active;
    private boolean      emailVerified;

    public UserProfileDto() {}

    public UserProfileDto(String userId,
                          String email,
                          String username,
                          String firstName,
                          String lastName,
                          String phoneNumber,
                          List<String> roles,
                          boolean active,
                          boolean emailVerified) {
        this.userId        = userId;
        this.email         = email;
        this.username      = username;
        this.firstName     = firstName;
        this.lastName      = lastName;
        this.phoneNumber   = phoneNumber;
        this.roles         = roles;
        this.active        = active;
        this.emailVerified = emailVerified;
    }

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

    public boolean isEmailVerified()                   { return emailVerified; }
    public void setEmailVerified(boolean emailVerified){ this.emailVerified = emailVerified; }
}

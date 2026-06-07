package com.foodly.identity.application.dto;

import java.util.List;

public class AuthResponseDto {

    private String accessToken;
    private String tokenType = "Bearer";
    private long expiresInMinutes;
    private String userId;
    private String email;
    private String username;
    private List<String> roles;

    public AuthResponseDto() {}

    public AuthResponseDto(String accessToken,
                           long expiresInMinutes,
                           String userId,
                           String email,
                           String username,
                           List<String> roles) {
        this.accessToken      = accessToken;
        this.expiresInMinutes = expiresInMinutes;
        this.userId           = userId;
        this.email            = email;
        this.username         = username;
        this.roles            = roles;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String       accessToken;
        private long         expiresInMinutes;
        private String       userId;
        private String       email;
        private String       username;
        private List<String> roles;

        public Builder accessToken(String token)            { this.accessToken = token; return this; }
        public Builder expiresInMinutes(long minutes)       { this.expiresInMinutes = minutes; return this; }
        public Builder userId(String userId)                { this.userId = userId; return this; }
        public Builder email(String email)                  { this.email = email; return this; }
        public Builder username(String username)            { this.username = username; return this; }
        public Builder roles(List<String> roles)            { this.roles = roles; return this; }

        public AuthResponseDto build() {
            return new AuthResponseDto(accessToken, expiresInMinutes, userId, email, username, roles);
        }
    }

    public String getAccessToken()                       { return accessToken; }
    public void setAccessToken(String accessToken)       { this.accessToken = accessToken; }

    public String getTokenType()                         { return tokenType; }
    public void setTokenType(String tokenType)           { this.tokenType = tokenType; }

    public long getExpiresInMinutes()                    { return expiresInMinutes; }
    public void setExpiresInMinutes(long expiresInMinutes) { this.expiresInMinutes = expiresInMinutes; }

    public String getUserId()                            { return userId; }
    public void setUserId(String userId)                 { this.userId = userId; }

    public String getEmail()                             { return email; }
    public void setEmail(String email)                   { this.email = email; }

    public String getUsername()                          { return username; }
    public void setUsername(String username)             { this.username = username; }

    public List<String> getRoles()                       { return roles; }
    public void setRoles(List<String> roles)             { this.roles = roles; }
}

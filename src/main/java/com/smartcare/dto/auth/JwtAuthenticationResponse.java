package com.smartcare.dto.auth;

public class JwtAuthenticationResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private Long userId;
    private String username;
    private String email;
    private boolean profileCompleted;
    private boolean tourCompleted;

    public JwtAuthenticationResponse(String accessToken, Long userId, String username, String email, 
                                   boolean profileCompleted, boolean tourCompleted) {
        this.accessToken = accessToken;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.profileCompleted = profileCompleted;
        this.tourCompleted = tourCompleted;
    }

    // Getters and Setters
    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public boolean isProfileCompleted() { return profileCompleted; }
    public void setProfileCompleted(boolean profileCompleted) { this.profileCompleted = profileCompleted; }

    public boolean isTourCompleted() { return tourCompleted; }
    public void setTourCompleted(boolean tourCompleted) { this.tourCompleted = tourCompleted; }
}

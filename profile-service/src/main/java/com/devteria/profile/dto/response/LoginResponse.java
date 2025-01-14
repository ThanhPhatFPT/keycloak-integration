package com.devteria.profile.dto.response;

import com.devteria.profile.entity.Profile;

public class LoginResponse {
    private Profile profile;
    private String token;

    // Constructor, Getter, Setter
    public LoginResponse(Profile profile, String token) {
        this.profile = profile;
        this.token = token;
    }

    // Getter, Setter
    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

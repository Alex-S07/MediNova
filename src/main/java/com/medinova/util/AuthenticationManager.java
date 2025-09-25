package com.medinova.util;

import com.medinova.model.User;

/**
 * Session management for authenticated users
 */
public class AuthenticationManager {
    private static AuthenticationManager instance;
    private User currentUser;
    private long loginTime;
    
    private AuthenticationManager() {}
    
    public static synchronized AuthenticationManager getInstance() {
        if (instance == null) {
            instance = new AuthenticationManager();
        }
        return instance;
    }
    
    public void login(User user) {
        this.currentUser = user;
        this.loginTime = System.currentTimeMillis();
    }
    
    public void logout() {
        this.currentUser = null;
        this.loginTime = 0;
    }
    
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public boolean hasRole(User.UserRole role) {
        return currentUser != null && currentUser.getRole() == role;
    }
    
    public boolean hasAnyRole(User.UserRole... roles) {
        if (currentUser == null) return false;
        
        for (User.UserRole role : roles) {
            if (currentUser.getRole() == role) {
                return true;
            }
        }
        return false;
    }
    
    public long getLoginTime() {
        return loginTime;
    }
    
    public long getSessionDuration() {
        return isLoggedIn() ? System.currentTimeMillis() - loginTime : 0;
    }
}
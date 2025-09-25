package com.medinova.controller;

import com.medinova.config.DatabaseConfig;
import com.medinova.model.User;
import com.medinova.util.PasswordUtil;

import java.sql.*;
import java.time.LocalDateTime;

/**
 * Controller for user authentication
 */
public class AuthenticationController {
    private final DatabaseConfig dbConfig;
    
    public AuthenticationController() {
        this.dbConfig = DatabaseConfig.getInstance();
    }
    
    /**
     * Authenticate user with username and password
     * @param username The username
     * @param password The plain text password
     * @return User object if authentication successful, null otherwise
     */
    public User authenticate(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND is_active = true";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String hashedPassword = rs.getString("password_hash");
                    
                    // Verify password
                    if (PasswordUtil.verifyPassword(password, hashedPassword)) {
                        // Create user object
                        User user = new User();
                        user.setId(rs.getInt("id"));
                        user.setUsername(rs.getString("username"));
                        user.setPasswordHash(hashedPassword);
                        user.setRole(User.UserRole.valueOf(rs.getString("role")));
                        user.setFirstName(rs.getString("first_name"));
                        user.setLastName(rs.getString("last_name"));
                        user.setEmail(rs.getString("email"));
                        user.setPhone(rs.getString("phone"));
                        user.setActive(rs.getBoolean("is_active"));
                        
                        // Set timestamps
                        Timestamp createdAt = rs.getTimestamp("created_at");
                        Timestamp updatedAt = rs.getTimestamp("updated_at");
                        if (createdAt != null) {
                            user.setCreatedAt(createdAt.toLocalDateTime());
                        }
                        if (updatedAt != null) {
                            user.setUpdatedAt(updatedAt.toLocalDateTime());
                        }
                        
                        return user;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Authentication error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Create a new user account
     * @param user The user to create
     * @param password The plain text password
     * @return true if user created successfully, false otherwise
     */
    public boolean createUser(User user, String password) {
        if (!PasswordUtil.isPasswordStrong(password)) {
            throw new IllegalArgumentException("Password does not meet security requirements");
        }
        
        String sql = "INSERT INTO users (username, password_hash, role, first_name, last_name, email, phone, is_active) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, PasswordUtil.hashPassword(password));
            stmt.setString(3, user.getRole().name());
            stmt.setString(4, user.getFirstName());
            stmt.setString(5, user.getLastName());
            stmt.setString(6, user.getEmail());
            stmt.setString(7, user.getPhone());
            stmt.setBoolean(8, user.isActive());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getInt(1));
                        user.setCreatedAt(LocalDateTime.now());
                        user.setUpdatedAt(LocalDateTime.now());
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("User creation error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Change user password
     * @param userId The user ID
     * @param oldPassword The current password
     * @param newPassword The new password
     * @return true if password changed successfully, false otherwise
     */
    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        if (!PasswordUtil.isPasswordStrong(newPassword)) {
            throw new IllegalArgumentException("New password does not meet security requirements");
        }
        
        // First verify old password
        String selectSql = "SELECT password_hash FROM users WHERE id = ?";
        String updateSql = "UPDATE users SET password_hash = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (Connection conn = dbConfig.getConnection()) {
            // Verify old password
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                selectStmt.setInt(1, userId);
                
                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (rs.next()) {
                        String currentHash = rs.getString("password_hash");
                        
                        if (!PasswordUtil.verifyPassword(oldPassword, currentHash)) {
                            return false; // Old password doesn't match
                        }
                    } else {
                        return false; // User not found
                    }
                }
            }
            
            // Update password
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setString(1, PasswordUtil.hashPassword(newPassword));
                updateStmt.setInt(2, userId);
                
                return updateStmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Password change error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Check if username is available
     * @param username The username to check
     * @return true if username is available, false otherwise
     */
    public boolean isUsernameAvailable(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Username check error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
}
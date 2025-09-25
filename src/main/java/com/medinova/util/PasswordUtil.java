package com.medinova.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility class for password hashing and verification
 */
public class PasswordUtil {
    private static final int BCRYPT_ROUNDS = 12;
    
    /**
     * Hash a password using BCrypt
     * @param password The plain text password
     * @return The hashed password
     */
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(BCRYPT_ROUNDS));
    }
    
    /**
     * Verify a password against its hash
     * @param password The plain text password
     * @param hashedPassword The hashed password to verify against
     * @return true if the password matches, false otherwise
     */
    public static boolean verifyPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
    
    /**
     * Check if a password meets minimum security requirements
     * @param password The password to check
     * @return true if password is strong enough, false otherwise
     */
    public static boolean isPasswordStrong(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }
        
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
        }
        
        return hasUpper && hasLower && hasDigit;
    }
}
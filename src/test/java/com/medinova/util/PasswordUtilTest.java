package com.medinova.util;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test class for PasswordUtil
 */
public class PasswordUtilTest {
    
    @Test
    public void testPasswordHashing() {
        String password = "testPassword123";
        String hashedPassword = PasswordUtil.hashPassword(password);
        
        assertNotNull("Hashed password should not be null", hashedPassword);
        assertNotEquals("Hashed password should be different from original", password, hashedPassword);
        assertTrue("Hashed password should start with $2a$", hashedPassword.startsWith("$2a$"));
    }
    
    @Test
    public void testPasswordVerification() {
        String password = "testPassword123";
        String hashedPassword = PasswordUtil.hashPassword(password);
        
        assertTrue("Password verification should succeed with correct password", 
                  PasswordUtil.verifyPassword(password, hashedPassword));
        assertFalse("Password verification should fail with incorrect password", 
                   PasswordUtil.verifyPassword("wrongPassword", hashedPassword));
    }
    
    @Test
    public void testPasswordStrengthValidation() {
        // Strong passwords
        assertTrue("Should accept strong password", PasswordUtil.isPasswordStrong("Password123"));
        assertTrue("Should accept another strong password", PasswordUtil.isPasswordStrong("MyStr0ngP@ss"));
        
        // Weak passwords
        assertFalse("Should reject short password", PasswordUtil.isPasswordStrong("12345"));
        assertFalse("Should reject password without uppercase", PasswordUtil.isPasswordStrong("password123"));
        assertFalse("Should reject password without lowercase", PasswordUtil.isPasswordStrong("PASSWORD123"));
        assertFalse("Should reject password without numbers", PasswordUtil.isPasswordStrong("Password"));
        assertFalse("Should reject null password", PasswordUtil.isPasswordStrong(null));
        assertFalse("Should reject empty password", PasswordUtil.isPasswordStrong(""));
    }
}
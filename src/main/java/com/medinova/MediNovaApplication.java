package com.medinova;

import com.formdev.flatlaf.FlatLightLaf;
import com.medinova.view.LoginFrame;
import com.medinova.config.DatabaseConfig;

import javax.swing.*;
import java.awt.*;

/**
 * Main application class for MediNova Hospital Management System
 */
public class MediNovaApplication {
    
    public static void main(String[] args) {
        // Set system properties for better appearance
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        
        // Set look and feel
        try {
            FlatLightLaf.setup();
        } catch (Exception e) {
            // Fallback to system look and feel
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        // Configure UI defaults
        configureUIDefaults();
        
        // Initialize database connection (test connection)
        testDatabaseConnection();
        
        // Start the application
        SwingUtilities.invokeLater(() -> {
            try {
                new LoginFrame().setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, 
                    "Failed to start application: " + e.getMessage(),
                    "Startup Error", 
                    JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });
    }
    
    private static void configureUIDefaults() {
        // Set default font
        Font defaultFont = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
        UIManager.put("defaultFont", defaultFont);
        
        // Set application-wide defaults
        UIManager.put("Button.font", defaultFont);
        UIManager.put("Label.font", defaultFont);
        UIManager.put("TextField.font", defaultFont);
        UIManager.put("TextArea.font", defaultFont);
        UIManager.put("Table.font", defaultFont);
        UIManager.put("Tree.font", defaultFont);
        UIManager.put("Menu.font", defaultFont);
        UIManager.put("MenuItem.font", defaultFont);
        
        // Set colors
        UIManager.put("Panel.background", Color.WHITE);
        UIManager.put("TextField.background", Color.WHITE);
        UIManager.put("TextArea.background", Color.WHITE);
        
        // Window icon (if available)
        // UIManager.put("OptionPane.informationIcon", new ImageIcon("icon.png"));
    }
    
    private static void testDatabaseConnection() {
        try {
            DatabaseConfig dbConfig = DatabaseConfig.getInstance();
            System.out.println("Database configuration loaded successfully");
            System.out.println("Database URL: " + dbConfig.getDatabaseUrl());
            
            // Test connection (will show error if database is not available)
            // This is non-blocking - app will still start even if DB is unavailable
            try {
                dbConfig.getConnection();
                System.out.println("Database connection test successful");
            } catch (Exception e) {
                System.err.println("Warning: Database connection failed: " + e.getMessage());
                System.err.println("Please ensure MySQL is running and the database is created");
            }
        } catch (Exception e) {
            System.err.println("Database configuration error: " + e.getMessage());
        }
    }
}
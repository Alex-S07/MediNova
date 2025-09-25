package com.medinova.view;

import com.medinova.model.User;
import com.medinova.util.AuthenticationManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Main dashboard window for the Hospital Management System
 */
public class MainDashboard extends JFrame {
    private AuthenticationManager authManager;
    private User currentUser;
    private JLabel userInfoLabel;
    private JPanel mainContentPanel;
    private CardLayout cardLayout;
    
    public MainDashboard() {
        this.authManager = AuthenticationManager.getInstance();
        this.currentUser = authManager.getCurrentUser();
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupFrame();
    }
    
    private void initializeComponents() {
        userInfoLabel = new JLabel();
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        
        updateUserInfo();
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Sidebar panel
        JPanel sidebarPanel = createSidebarPanel();
        add(sidebarPanel, BorderLayout.WEST);
        
        // Main content panel
        setupMainContent();
        add(mainContentPanel, BorderLayout.CENTER);
        
        // Status bar
        JPanel statusPanel = createStatusPanel();
        add(statusPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        headerPanel.setPreferredSize(new Dimension(0, 60));
        
        // Title
        JLabel titleLabel = new JLabel("MediNova Hospital Management System");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // User info and logout
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setOpaque(false);
        
        userInfoLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        userInfoLabel.setForeground(Color.WHITE);
        userPanel.add(userInfoLabel);
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.setPreferredSize(new Dimension(80, 30));
        logoutButton.setBackground(Color.WHITE);
        logoutButton.setForeground(new Color(70, 130, 180));
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(e -> logout());
        userPanel.add(logoutButton);
        
        headerPanel.add(userPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createSidebarPanel() {
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(new Color(245, 245, 245));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        sidebarPanel.setPreferredSize(new Dimension(200, 0));
        
        // Navigation buttons based on user role
        addNavigationButtons(sidebarPanel);
        
        return sidebarPanel;
    }
    
    private void addNavigationButtons(JPanel sidebarPanel) {
        // Dashboard button (always visible)
        addNavButton(sidebarPanel, "Dashboard", "dashboard", true);
        
        // Role-based navigation
        User.UserRole role = currentUser.getRole();
        
        switch (role) {
            case ADMIN:
                addNavButton(sidebarPanel, "User Management", "users", true);
                addNavButton(sidebarPanel, "Patients", "patients", true);
                addNavButton(sidebarPanel, "Doctors", "doctors", true);
                addNavButton(sidebarPanel, "Appointments", "appointments", true);
                addNavButton(sidebarPanel, "Billing", "billing", true);
                addNavButton(sidebarPanel, "Pharmacy", "pharmacy", true);
                addNavButton(sidebarPanel, "Reports", "reports", true);
                addNavButton(sidebarPanel, "Audit Log", "audit", true);
                break;
                
            case DOCTOR:
                addNavButton(sidebarPanel, "My Appointments", "appointments", true);
                addNavButton(sidebarPanel, "Patients", "patients", true);
                addNavButton(sidebarPanel, "Prescriptions", "prescriptions", false);
                break;
                
            case NURSE:
                addNavButton(sidebarPanel, "Patients", "patients", true);
                addNavButton(sidebarPanel, "Appointments", "appointments", true);
                addNavButton(sidebarPanel, "Medicine Inventory", "pharmacy", true);
                break;
                
            case RECEPTIONIST:
                addNavButton(sidebarPanel, "Patients", "patients", true);
                addNavButton(sidebarPanel, "Appointments", "appointments", true);
                addNavButton(sidebarPanel, "Billing", "billing", true);
                break;
                
            case PHARMACIST:
                addNavButton(sidebarPanel, "Pharmacy", "pharmacy", true);
                addNavButton(sidebarPanel, "Inventory", "inventory", false);
                addNavButton(sidebarPanel, "Prescriptions", "prescriptions", false);
                break;
        }
        
        // Settings (always visible)
        sidebarPanel.add(Box.createVerticalGlue());
        addNavButton(sidebarPanel, "Settings", "settings", false);
    }
    
    private void addNavButton(JPanel parent, String text, String action, boolean implemented) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(180, 35));
        button.setPreferredSize(new Dimension(180, 35));
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        if (!implemented) {
            button.setEnabled(false);
            button.setToolTipText("Coming soon");
        } else {
            button.addActionListener(e -> navigateToPanel(action));
        }
        
        parent.add(button);
        parent.add(Box.createVerticalStrut(5));
    }
    
    private void setupMainContent() {
        // Dashboard panel
        JPanel dashboardPanel = createDashboardPanel();
        mainContentPanel.add(dashboardPanel, "dashboard");
        
        // Placeholder panels for other modules
        mainContentPanel.add(createPlaceholderPanel("User Management"), "users");
        mainContentPanel.add(createPlaceholderPanel("Patient Management"), "patients");
        mainContentPanel.add(createPlaceholderPanel("Doctor Management"), "doctors");
        mainContentPanel.add(createPlaceholderPanel("Appointment Management"), "appointments");
        mainContentPanel.add(createPlaceholderPanel("Billing System"), "billing");
        mainContentPanel.add(createPlaceholderPanel("Pharmacy Management"), "pharmacy");
        mainContentPanel.add(createPlaceholderPanel("Reports & Analytics"), "reports");
        mainContentPanel.add(createPlaceholderPanel("Audit Log"), "audit");
        mainContentPanel.add(createPlaceholderPanel("Settings"), "settings");
        
        // Show dashboard by default
        cardLayout.show(mainContentPanel, "dashboard");
    }
    
    private JPanel createDashboardPanel() {
        JPanel dashboardPanel = new JPanel(new BorderLayout());
        dashboardPanel.setBackground(Color.WHITE);
        dashboardPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Welcome message
        JLabel welcomeLabel = new JLabel("Welcome to MediNova, " + currentUser.getFullName() + "!");
        welcomeLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        dashboardPanel.add(welcomeLabel, BorderLayout.NORTH);
        
        // Quick stats panel
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        
        // Stats cards (placeholder data)
        statsPanel.add(createStatCard("Total Patients", "0", Color.BLUE));
        statsPanel.add(createStatCard("Today's Appointments", "0", Color.GREEN));
        statsPanel.add(createStatCard("Available Doctors", "0", Color.ORANGE));
        statsPanel.add(createStatCard("Pending Bills", "0", Color.RED));
        
        dashboardPanel.add(statsPanel, BorderLayout.CENTER);
        
        // System info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createTitledBorder("System Information"));
        
        infoPanel.add(new JLabel("Role: " + currentUser.getRole().getDisplayName()));
        infoPanel.add(new JLabel("Login Time: " + new java.util.Date(authManager.getLoginTime())));
        infoPanel.add(new JLabel("Database Status: Connected"));
        
        dashboardPanel.add(infoPanel, BorderLayout.SOUTH);
        
        return dashboardPanel;
    }
    
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        titleLabel.setForeground(color);
        
        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 32));
        valueLabel.setForeground(color);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createPlaceholderPanel(String moduleName) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        
        JLabel label = new JLabel(moduleName + " Module", SwingConstants.CENTER);
        label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        label.setForeground(Color.GRAY);
        
        JLabel comingSoonLabel = new JLabel("Implementation in progress...", SwingConstants.CENTER);
        comingSoonLabel.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 14));
        comingSoonLabel.setForeground(Color.GRAY);
        
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.add(label);
        content.add(Box.createVerticalStrut(10));
        content.add(comingSoonLabel);
        
        panel.add(content, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createStatusPanel() {
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        statusPanel.setBackground(Color.LIGHT_GRAY);
        statusPanel.setPreferredSize(new Dimension(0, 25));
        
        JLabel statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        statusPanel.add(statusLabel);
        
        return statusPanel;
    }
    
    private void setupEventHandlers() {
        // Window closing event
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                logout();
            }
        });
    }
    
    private void setupFrame() {
        setTitle("MediNova - Hospital Management System");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }
    
    private void updateUserInfo() {
        if (currentUser != null) {
            userInfoLabel.setText(currentUser.getFullName() + " (" + currentUser.getRole().getDisplayName() + ")");
        }
    }
    
    private void navigateToPanel(String panelName) {
        cardLayout.show(mainContentPanel, panelName);
    }
    
    private void logout() {
        int option = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION
        );
        
        if (option == JOptionPane.YES_OPTION) {
            authManager.logout();
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
}
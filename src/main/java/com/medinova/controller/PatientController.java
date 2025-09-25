package com.medinova.controller;

import com.medinova.config.DatabaseConfig;
import com.medinova.model.Patient;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for Patient management operations
 */
public class PatientController {
    private final DatabaseConfig dbConfig;
    
    public PatientController() {
        this.dbConfig = DatabaseConfig.getInstance();
    }
    
    /**
     * Create a new patient
     * @param patient The patient to create
     * @return true if patient created successfully, false otherwise
     */
    public boolean createPatient(Patient patient) {
        String sql = "INSERT INTO patients (patient_id, first_name, last_name, date_of_birth, gender, " +
                    "phone, email, address, emergency_contact, emergency_phone, blood_group, allergies, medical_history) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, patient.getPatientId());
            stmt.setString(2, patient.getFirstName());
            stmt.setString(3, patient.getLastName());
            stmt.setDate(4, Date.valueOf(patient.getDateOfBirth()));
            stmt.setString(5, patient.getGender().name());
            stmt.setString(6, patient.getPhone());
            stmt.setString(7, patient.getEmail());
            stmt.setString(8, patient.getAddress());
            stmt.setString(9, patient.getEmergencyContact());
            stmt.setString(10, patient.getEmergencyPhone());
            stmt.setString(11, patient.getBloodGroup());
            stmt.setString(12, patient.getAllergies());
            stmt.setString(13, patient.getMedicalHistory());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        patient.setId(generatedKeys.getInt(1));
                        patient.setCreatedAt(LocalDateTime.now());
                        patient.setUpdatedAt(LocalDateTime.now());
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Patient creation error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Update an existing patient
     * @param patient The patient to update
     * @return true if patient updated successfully, false otherwise
     */
    public boolean updatePatient(Patient patient) {
        String sql = "UPDATE patients SET first_name = ?, last_name = ?, date_of_birth = ?, gender = ?, " +
                    "phone = ?, email = ?, address = ?, emergency_contact = ?, emergency_phone = ?, " +
                    "blood_group = ?, allergies = ?, medical_history = ?, updated_at = CURRENT_TIMESTAMP " +
                    "WHERE id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, patient.getFirstName());
            stmt.setString(2, patient.getLastName());
            stmt.setDate(3, Date.valueOf(patient.getDateOfBirth()));
            stmt.setString(4, patient.getGender().name());
            stmt.setString(5, patient.getPhone());
            stmt.setString(6, patient.getEmail());
            stmt.setString(7, patient.getAddress());
            stmt.setString(8, patient.getEmergencyContact());
            stmt.setString(9, patient.getEmergencyPhone());
            stmt.setString(10, patient.getBloodGroup());
            stmt.setString(11, patient.getAllergies());
            stmt.setString(12, patient.getMedicalHistory());
            stmt.setInt(13, patient.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Patient update error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Delete a patient
     * @param patientId The patient ID to delete
     * @return true if patient deleted successfully, false otherwise
     */
    public boolean deletePatient(int patientId) {
        String sql = "DELETE FROM patients WHERE id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, patientId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Patient deletion error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Find patient by ID
     * @param patientId The patient ID
     * @return Patient object if found, null otherwise
     */
    public Patient findPatientById(int patientId) {
        String sql = "SELECT * FROM patients WHERE id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, patientId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createPatientFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Patient search error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Find patient by patient ID
     * @param patientId The patient ID string
     * @return Patient object if found, null otherwise
     */
    public Patient findPatientByPatientId(String patientId) {
        String sql = "SELECT * FROM patients WHERE patient_id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, patientId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createPatientFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Patient search error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Search patients by name
     * @param searchTerm The search term (first name or last name)
     * @return List of matching patients
     */
    public List<Patient> searchPatients(String searchTerm) {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM patients WHERE first_name LIKE ? OR last_name LIKE ? OR patient_id LIKE ? " +
                    "ORDER BY last_name, first_name";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + searchTerm + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    patients.add(createPatientFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Patient search error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return patients;
    }
    
    /**
     * Get all patients
     * @return List of all patients
     */
    public List<Patient> getAllPatients() {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM patients ORDER BY last_name, first_name";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                patients.add(createPatientFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Get all patients error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return patients;
    }
    
    /**
     * Generate next patient ID
     * @return Next available patient ID
     */
    public String generatePatientId() {
        String sql = "SELECT MAX(CAST(SUBSTRING(patient_id, 2) AS UNSIGNED)) as max_id FROM patients WHERE patient_id REGEXP '^P[0-9]+$'";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                int maxId = rs.getInt("max_id");
                return String.format("P%06d", maxId + 1);
            }
        } catch (SQLException e) {
            System.err.println("Patient ID generation error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return "P000001"; // Default first ID
    }
    
    /**
     * Check if patient ID is available
     * @param patientId The patient ID to check
     * @return true if available, false otherwise
     */
    public boolean isPatientIdAvailable(String patientId) {
        String sql = "SELECT COUNT(*) FROM patients WHERE patient_id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, patientId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Patient ID check error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    private Patient createPatientFromResultSet(ResultSet rs) throws SQLException {
        Patient patient = new Patient();
        patient.setId(rs.getInt("id"));
        patient.setPatientId(rs.getString("patient_id"));
        patient.setFirstName(rs.getString("first_name"));
        patient.setLastName(rs.getString("last_name"));
        patient.setDateOfBirth(rs.getDate("date_of_birth").toLocalDate());
        patient.setGender(Patient.Gender.valueOf(rs.getString("gender")));
        patient.setPhone(rs.getString("phone"));
        patient.setEmail(rs.getString("email"));
        patient.setAddress(rs.getString("address"));
        patient.setEmergencyContact(rs.getString("emergency_contact"));
        patient.setEmergencyPhone(rs.getString("emergency_phone"));
        patient.setBloodGroup(rs.getString("blood_group"));
        patient.setAllergies(rs.getString("allergies"));
        patient.setMedicalHistory(rs.getString("medical_history"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (createdAt != null) {
            patient.setCreatedAt(createdAt.toLocalDateTime());
        }
        if (updatedAt != null) {
            patient.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return patient;
    }
}
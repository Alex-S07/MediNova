package com.medinova.controller;

import com.medinova.config.DatabaseConfig;
import com.medinova.model.Doctor;
import com.medinova.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for Doctor management operations
 */
public class DoctorController {
    private final DatabaseConfig dbConfig;
    
    public DoctorController() {
        this.dbConfig = DatabaseConfig.getInstance();
    }
    
    /**
     * Create a new doctor
     * @param doctor The doctor to create
     * @return true if doctor created successfully, false otherwise
     */
    public boolean createDoctor(Doctor doctor) {
        String sql = "INSERT INTO doctors (user_id, doctor_id, specialization, qualification, license_number, " +
                    "department, consultation_fee, available_from, available_to) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, doctor.getUserId());
            stmt.setString(2, doctor.getDoctorId());
            stmt.setString(3, doctor.getSpecialization());
            stmt.setString(4, doctor.getQualification());
            stmt.setString(5, doctor.getLicenseNumber());
            stmt.setString(6, doctor.getDepartment());
            stmt.setBigDecimal(7, doctor.getConsultationFee());
            
            if (doctor.getAvailableFrom() != null) {
                stmt.setTime(8, Time.valueOf(doctor.getAvailableFrom()));
            } else {
                stmt.setNull(8, Types.TIME);
            }
            
            if (doctor.getAvailableTo() != null) {
                stmt.setTime(9, Time.valueOf(doctor.getAvailableTo()));
            } else {
                stmt.setNull(9, Types.TIME);
            }
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        doctor.setId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Doctor creation error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Update an existing doctor
     * @param doctor The doctor to update
     * @return true if doctor updated successfully, false otherwise
     */
    public boolean updateDoctor(Doctor doctor) {
        String sql = "UPDATE doctors SET specialization = ?, qualification = ?, license_number = ?, " +
                    "department = ?, consultation_fee = ?, available_from = ?, available_to = ? " +
                    "WHERE id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, doctor.getSpecialization());
            stmt.setString(2, doctor.getQualification());
            stmt.setString(3, doctor.getLicenseNumber());
            stmt.setString(4, doctor.getDepartment());
            stmt.setBigDecimal(5, doctor.getConsultationFee());
            
            if (doctor.getAvailableFrom() != null) {
                stmt.setTime(6, Time.valueOf(doctor.getAvailableFrom()));
            } else {
                stmt.setNull(6, Types.TIME);
            }
            
            if (doctor.getAvailableTo() != null) {
                stmt.setTime(7, Time.valueOf(doctor.getAvailableTo()));
            } else {
                stmt.setNull(7, Types.TIME);
            }
            
            stmt.setInt(8, doctor.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Doctor update error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Delete a doctor
     * @param doctorId The doctor ID to delete
     * @return true if doctor deleted successfully, false otherwise
     */
    public boolean deleteDoctor(int doctorId) {
        String sql = "DELETE FROM doctors WHERE id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, doctorId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Doctor deletion error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Find doctor by ID
     * @param doctorId The doctor ID
     * @return Doctor object if found, null otherwise
     */
    public Doctor findDoctorById(int doctorId) {
        String sql = "SELECT d.*, u.username, u.first_name, u.last_name, u.email, u.phone, u.role " +
                    "FROM doctors d " +
                    "JOIN users u ON d.user_id = u.id " +
                    "WHERE d.id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, doctorId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createDoctorFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Doctor search error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Find doctor by doctor ID
     * @param doctorId The doctor ID string
     * @return Doctor object if found, null otherwise
     */
    public Doctor findDoctorByDoctorId(String doctorId) {
        String sql = "SELECT d.*, u.username, u.first_name, u.last_name, u.email, u.phone, u.role " +
                    "FROM doctors d " +
                    "JOIN users u ON d.user_id = u.id " +
                    "WHERE d.doctor_id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, doctorId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createDoctorFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Doctor search error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Find doctor by user ID
     * @param userId The user ID
     * @return Doctor object if found, null otherwise
     */
    public Doctor findDoctorByUserId(int userId) {
        String sql = "SELECT d.*, u.username, u.first_name, u.last_name, u.email, u.phone, u.role " +
                    "FROM doctors d " +
                    "JOIN users u ON d.user_id = u.id " +
                    "WHERE d.user_id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createDoctorFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Doctor search error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Search doctors by name or specialization
     * @param searchTerm The search term
     * @return List of matching doctors
     */
    public List<Doctor> searchDoctors(String searchTerm) {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT d.*, u.username, u.first_name, u.last_name, u.email, u.phone, u.role " +
                    "FROM doctors d " +
                    "JOIN users u ON d.user_id = u.id " +
                    "WHERE u.first_name LIKE ? OR u.last_name LIKE ? OR d.specialization LIKE ? " +
                    "OR d.doctor_id LIKE ? OR d.department LIKE ? " +
                    "ORDER BY u.last_name, u.first_name";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + searchTerm + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            stmt.setString(4, searchPattern);
            stmt.setString(5, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    doctors.add(createDoctorFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Doctor search error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return doctors;
    }
    
    /**
     * Get all doctors
     * @return List of all doctors
     */
    public List<Doctor> getAllDoctors() {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT d.*, u.username, u.first_name, u.last_name, u.email, u.phone, u.role " +
                    "FROM doctors d " +
                    "JOIN users u ON d.user_id = u.id " +
                    "WHERE u.is_active = true " +
                    "ORDER BY u.last_name, u.first_name";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                doctors.add(createDoctorFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Get all doctors error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return doctors;
    }
    
    /**
     * Get doctors by specialization
     * @param specialization The specialization
     * @return List of doctors with the specified specialization
     */
    public List<Doctor> getDoctorsBySpecialization(String specialization) {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT d.*, u.username, u.first_name, u.last_name, u.email, u.phone, u.role " +
                    "FROM doctors d " +
                    "JOIN users u ON d.user_id = u.id " +
                    "WHERE d.specialization = ? AND u.is_active = true " +
                    "ORDER BY u.last_name, u.first_name";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, specialization);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    doctors.add(createDoctorFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Get doctors by specialization error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return doctors;
    }
    
    /**
     * Generate next doctor ID
     * @return Next available doctor ID
     */
    public String generateDoctorId() {
        String sql = "SELECT MAX(CAST(SUBSTRING(doctor_id, 3) AS UNSIGNED)) as max_id FROM doctors WHERE doctor_id REGEXP '^DR[0-9]+$'";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                int maxId = rs.getInt("max_id");
                return String.format("DR%04d", maxId + 1);
            }
        } catch (SQLException e) {
            System.err.println("Doctor ID generation error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return "DR0001"; // Default first ID
    }
    
    /**
     * Check if doctor ID is available
     * @param doctorId The doctor ID to check
     * @return true if available, false otherwise
     */
    public boolean isDoctorIdAvailable(String doctorId) {
        String sql = "SELECT COUNT(*) FROM doctors WHERE doctor_id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, doctorId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Doctor ID check error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Check if license number is available
     * @param licenseNumber The license number to check
     * @return true if available, false otherwise
     */
    public boolean isLicenseNumberAvailable(String licenseNumber) {
        String sql = "SELECT COUNT(*) FROM doctors WHERE license_number = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, licenseNumber);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("License number check error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    private Doctor createDoctorFromResultSet(ResultSet rs) throws SQLException {
        Doctor doctor = new Doctor();
        doctor.setId(rs.getInt("id"));
        doctor.setUserId(rs.getInt("user_id"));
        doctor.setDoctorId(rs.getString("doctor_id"));
        doctor.setSpecialization(rs.getString("specialization"));
        doctor.setQualification(rs.getString("qualification"));
        doctor.setLicenseNumber(rs.getString("license_number"));
        doctor.setDepartment(rs.getString("department"));
        doctor.setConsultationFee(rs.getBigDecimal("consultation_fee"));
        
        Time availableFrom = rs.getTime("available_from");
        Time availableTo = rs.getTime("available_to");
        if (availableFrom != null) {
            doctor.setAvailableFrom(availableFrom.toLocalTime());
        }
        if (availableTo != null) {
            doctor.setAvailableTo(availableTo.toLocalTime());
        }
        
        // Create associated User object
        User user = new User();
        user.setId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setRole(User.UserRole.valueOf(rs.getString("role")));
        
        doctor.setUser(user);
        
        return doctor;
    }
}
package com.medinova.controller;

import com.medinova.config.DatabaseConfig;
import com.medinova.model.Appointment;
import com.medinova.model.Doctor;
import com.medinova.model.Patient;
import com.medinova.model.User;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for Appointment management operations with concurrency control
 */
public class AppointmentController {
    private final DatabaseConfig dbConfig;
    private final PatientController patientController;
    private final DoctorController doctorController;
    
    public AppointmentController() {
        this.dbConfig = DatabaseConfig.getInstance();
        this.patientController = new PatientController();
        this.doctorController = new DoctorController();
    }
    
    /**
     * Create a new appointment with concurrency control
     * @param appointment The appointment to create
     * @return true if appointment created successfully, false otherwise
     */
    public boolean createAppointment(Appointment appointment) {
        // Use transaction for concurrency control
        Connection conn = null;
        try {
            conn = dbConfig.getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            // Check for conflicting appointments with row-level locking
            String checkSql = "SELECT COUNT(*) FROM appointments " +
                             "WHERE doctor_id = ? AND appointment_date = ? AND appointment_time = ? " +
                             "AND status NOT IN ('CANCELLED') FOR UPDATE";
            
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, appointment.getDoctorId());
                checkStmt.setDate(2, Date.valueOf(appointment.getAppointmentDate()));
                checkStmt.setTime(3, Time.valueOf(appointment.getAppointmentTime()));
                
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        conn.rollback();
                        return false; // Conflict detected
                    }
                }
            }
            
            // Insert the appointment
            String insertSql = "INSERT INTO appointments (appointment_id, patient_id, doctor_id, appointment_date, " +
                              "appointment_time, status, reason, notes, created_by) " +
                              "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                insertStmt.setString(1, appointment.getAppointmentId());
                insertStmt.setInt(2, appointment.getPatientId());
                insertStmt.setInt(3, appointment.getDoctorId());
                insertStmt.setDate(4, Date.valueOf(appointment.getAppointmentDate()));
                insertStmt.setTime(5, Time.valueOf(appointment.getAppointmentTime()));
                insertStmt.setString(6, appointment.getStatus().name());
                insertStmt.setString(7, appointment.getReason());
                insertStmt.setString(8, appointment.getNotes());
                insertStmt.setInt(9, appointment.getCreatedBy());
                
                int rowsAffected = insertStmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            appointment.setId(generatedKeys.getInt(1));
                            appointment.setCreatedAt(LocalDateTime.now());
                            appointment.setUpdatedAt(LocalDateTime.now());
                            
                            conn.commit(); // Commit transaction
                            return true;
                        }
                    }
                }
            }
            
            conn.rollback();
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            System.err.println("Appointment creation error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    dbConfig.releaseConnection(conn);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return false;
    }
    
    /**
     * Update an existing appointment
     * @param appointment The appointment to update
     * @return true if appointment updated successfully, false otherwise
     */
    public boolean updateAppointment(Appointment appointment) {
        String sql = "UPDATE appointments SET appointment_date = ?, appointment_time = ?, status = ?, " +
                    "reason = ?, notes = ?, updated_at = CURRENT_TIMESTAMP " +
                    "WHERE id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(appointment.getAppointmentDate()));
            stmt.setTime(2, Time.valueOf(appointment.getAppointmentTime()));
            stmt.setString(3, appointment.getStatus().name());
            stmt.setString(4, appointment.getReason());
            stmt.setString(5, appointment.getNotes());
            stmt.setInt(6, appointment.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Appointment update error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Cancel an appointment
     * @param appointmentId The appointment ID to cancel
     * @return true if appointment cancelled successfully, false otherwise
     */
    public boolean cancelAppointment(int appointmentId) {
        String sql = "UPDATE appointments SET status = 'CANCELLED', updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, appointmentId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Appointment cancellation error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Find appointment by ID
     * @param appointmentId The appointment ID
     * @return Appointment object if found, null otherwise
     */
    public Appointment findAppointmentById(int appointmentId) {
        String sql = "SELECT a.*, " +
                    "p.patient_id, p.first_name as p_first_name, p.last_name as p_last_name, " +
                    "d.doctor_id, u.first_name as d_first_name, u.last_name as d_last_name, d.specialization " +
                    "FROM appointments a " +
                    "JOIN patients p ON a.patient_id = p.id " +
                    "JOIN doctors d ON a.doctor_id = d.id " +
                    "JOIN users u ON d.user_id = u.id " +
                    "WHERE a.id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, appointmentId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createAppointmentFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Appointment search error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Find appointment by appointment ID
     * @param appointmentId The appointment ID string
     * @return Appointment object if found, null otherwise
     */
    public Appointment findAppointmentByAppointmentId(String appointmentId) {
        String sql = "SELECT a.*, " +
                    "p.patient_id, p.first_name as p_first_name, p.last_name as p_last_name, " +
                    "d.doctor_id, u.first_name as d_first_name, u.last_name as d_last_name, d.specialization " +
                    "FROM appointments a " +
                    "JOIN patients p ON a.patient_id = p.id " +
                    "JOIN doctors d ON a.doctor_id = d.id " +
                    "JOIN users u ON d.user_id = u.id " +
                    "WHERE a.appointment_id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, appointmentId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createAppointmentFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Appointment search error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Get appointments for a specific doctor on a specific date
     * @param doctorId The doctor ID
     * @param date The date
     * @return List of appointments
     */
    public List<Appointment> getAppointmentsByDoctorAndDate(int doctorId, LocalDate date) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT a.*, " +
                    "p.patient_id, p.first_name as p_first_name, p.last_name as p_last_name, " +
                    "d.doctor_id, u.first_name as d_first_name, u.last_name as d_last_name, d.specialization " +
                    "FROM appointments a " +
                    "JOIN patients p ON a.patient_id = p.id " +
                    "JOIN doctors d ON a.doctor_id = d.id " +
                    "JOIN users u ON d.user_id = u.id " +
                    "WHERE a.doctor_id = ? AND a.appointment_date = ? " +
                    "ORDER BY a.appointment_time";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, doctorId);
            stmt.setDate(2, Date.valueOf(date));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    appointments.add(createAppointmentFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Get appointments by doctor and date error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return appointments;
    }
    
    /**
     * Get appointments for a specific patient
     * @param patientId The patient ID
     * @return List of appointments
     */
    public List<Appointment> getAppointmentsByPatient(int patientId) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT a.*, " +
                    "p.patient_id, p.first_name as p_first_name, p.last_name as p_last_name, " +
                    "d.doctor_id, u.first_name as d_first_name, u.last_name as d_last_name, d.specialization " +
                    "FROM appointments a " +
                    "JOIN patients p ON a.patient_id = p.id " +
                    "JOIN doctors d ON a.doctor_id = d.id " +
                    "JOIN users u ON d.user_id = u.id " +
                    "WHERE a.patient_id = ? " +
                    "ORDER BY a.appointment_date DESC, a.appointment_time DESC";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, patientId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    appointments.add(createAppointmentFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Get appointments by patient error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return appointments;
    }
    
    /**
     * Get today's appointments
     * @return List of today's appointments
     */
    public List<Appointment> getTodaysAppointments() {
        return getAppointmentsByDate(LocalDate.now());
    }
    
    /**
     * Get appointments by date
     * @param date The date
     * @return List of appointments for the specified date
     */
    public List<Appointment> getAppointmentsByDate(LocalDate date) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT a.*, " +
                    "p.patient_id, p.first_name as p_first_name, p.last_name as p_last_name, " +
                    "d.doctor_id, u.first_name as d_first_name, u.last_name as d_last_name, d.specialization " +
                    "FROM appointments a " +
                    "JOIN patients p ON a.patient_id = p.id " +
                    "JOIN doctors d ON a.doctor_id = d.id " +
                    "JOIN users u ON d.user_id = u.id " +
                    "WHERE a.appointment_date = ? " +
                    "ORDER BY a.appointment_time";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(date));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    appointments.add(createAppointmentFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Get appointments by date error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return appointments;
    }
    
    /**
     * Check if doctor is available at specific date and time
     * @param doctorId The doctor ID
     * @param date The date
     * @param time The time
     * @return true if available, false otherwise
     */
    public boolean isDoctorAvailable(int doctorId, LocalDate date, LocalTime time) {
        String sql = "SELECT COUNT(*) FROM appointments " +
                    "WHERE doctor_id = ? AND appointment_date = ? AND appointment_time = ? " +
                    "AND status NOT IN ('CANCELLED')";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, doctorId);
            stmt.setDate(2, Date.valueOf(date));
            stmt.setTime(3, Time.valueOf(time));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Doctor availability check error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Generate next appointment ID
     * @return Next available appointment ID
     */
    public String generateAppointmentId() {
        String sql = "SELECT MAX(CAST(SUBSTRING(appointment_id, 4) AS UNSIGNED)) as max_id FROM appointments WHERE appointment_id REGEXP '^APT[0-9]+$'";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                int maxId = rs.getInt("max_id");
                return String.format("APT%06d", maxId + 1);
            }
        } catch (SQLException e) {
            System.err.println("Appointment ID generation error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return "APT000001"; // Default first ID
    }
    
    /**
     * Search appointments by multiple criteria
     * @param searchTerm The search term
     * @return List of matching appointments
     */
    public List<Appointment> searchAppointments(String searchTerm) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT a.*, " +
                    "p.patient_id, p.first_name as p_first_name, p.last_name as p_last_name, " +
                    "d.doctor_id, u.first_name as d_first_name, u.last_name as d_last_name, d.specialization " +
                    "FROM appointments a " +
                    "JOIN patients p ON a.patient_id = p.id " +
                    "JOIN doctors d ON a.doctor_id = d.id " +
                    "JOIN users u ON d.user_id = u.id " +
                    "WHERE a.appointment_id LIKE ? OR p.first_name LIKE ? OR p.last_name LIKE ? " +
                    "OR u.first_name LIKE ? OR u.last_name LIKE ? OR d.specialization LIKE ? " +
                    "ORDER BY a.appointment_date DESC, a.appointment_time DESC";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + searchTerm + "%";
            for (int i = 1; i <= 6; i++) {
                stmt.setString(i, searchPattern);
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    appointments.add(createAppointmentFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Appointment search error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return appointments;
    }
    
    private Appointment createAppointmentFromResultSet(ResultSet rs) throws SQLException {
        Appointment appointment = new Appointment();
        appointment.setId(rs.getInt("id"));
        appointment.setAppointmentId(rs.getString("appointment_id"));
        appointment.setPatientId(rs.getInt("patient_id"));
        appointment.setDoctorId(rs.getInt("doctor_id"));
        appointment.setAppointmentDate(rs.getDate("appointment_date").toLocalDate());
        appointment.setAppointmentTime(rs.getTime("appointment_time").toLocalTime());
        appointment.setStatus(Appointment.AppointmentStatus.valueOf(rs.getString("status")));
        appointment.setReason(rs.getString("reason"));
        appointment.setNotes(rs.getString("notes"));
        appointment.setCreatedBy(rs.getInt("created_by"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (createdAt != null) {
            appointment.setCreatedAt(createdAt.toLocalDateTime());
        }
        if (updatedAt != null) {
            appointment.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        // Create simplified Patient object
        Patient patient = new Patient();
        patient.setId(appointment.getPatientId());
        patient.setPatientId(rs.getString("patient_id"));
        patient.setFirstName(rs.getString("p_first_name"));
        patient.setLastName(rs.getString("p_last_name"));
        appointment.setPatient(patient);
        
        // Create simplified Doctor object
        Doctor doctor = new Doctor();
        doctor.setId(appointment.getDoctorId());
        doctor.setDoctorId(rs.getString("doctor_id"));
        doctor.setSpecialization(rs.getString("specialization"));
        
        User doctorUser = new User();
        doctorUser.setFirstName(rs.getString("d_first_name"));
        doctorUser.setLastName(rs.getString("d_last_name"));
        doctor.setUser(doctorUser);
        
        appointment.setDoctor(doctor);
        
        return appointment;
    }
}
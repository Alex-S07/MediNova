# MediNova Hospital Management System
*Powering the future of healthcare*

A comprehensive Java Swing desktop application backed by a MySQL database, designed to streamline hospital operations. Built following the MVC (Model-View-Controller) architecture, the project integrates key modules including Patient and Doctor management, Appointment Scheduling with concurrency control, Billing, Pharmacy Inventory, Role-Based Access, and advanced Reporting & Auditing features.

## Features

### 🏥 Core Modules
- **Patient Management**: Complete patient registration, medical history, and profile management
- **Doctor Management**: Doctor profiles, specializations, qualifications, and availability scheduling
- **Appointment Scheduling**: Advanced scheduling system with concurrency control to prevent conflicts
- **Billing System**: Comprehensive billing with itemized invoices, payment tracking, and multiple payment methods
- **Pharmacy Inventory**: Medicine inventory management, stock tracking, expiry monitoring, and low-stock alerts
- **User Management**: Role-based access control with different permission levels

### 🔐 Security & Access Control
- **Role-Based Access**: Different roles (Admin, Doctor, Nurse, Receptionist, Pharmacist) with specific permissions
- **Secure Authentication**: BCrypt password hashing and secure session management
- **Audit Trail**: Complete audit logging for all system activities

### 🎨 User Interface
- **Modern UI**: Clean, intuitive Java Swing interface with FlatLaf look and feel
- **Responsive Design**: Adaptable layouts for different screen sizes
- **Role-Based Navigation**: Dynamic menus based on user roles and permissions

### 🏗️ Technical Architecture
- **MVC Pattern**: Clean separation of concerns with Model-View-Controller architecture
- **Database Integration**: MySQL database with connection pooling and transaction management
- **Concurrent Operations**: Thread-safe appointment scheduling with row-level locking
- **Error Handling**: Comprehensive error handling and user feedback

## Prerequisites

Before running the application, ensure you have the following installed:

- **Java 11 or higher**: [Download Java](https://adoptopenjdk.net/)
- **MySQL 8.0 or higher**: [Download MySQL](https://dev.mysql.com/downloads/mysql/)
- **Maven 3.6 or higher**: [Download Maven](https://maven.apache.org/download.cgi)

## Installation & Setup

### 1. Database Setup

1. **Start MySQL server** and connect as root user
2. **Create the database** by running the schema file:
   ```sql
   mysql -u root -p < src/main/resources/schema.sql
   ```
   Or manually execute the SQL commands in `src/main/resources/schema.sql`

3. **Update database credentials** in `src/main/resources/database.properties`:
   ```properties
   db.url=jdbc:mysql://localhost:3306/medinova_hospital
   db.username=your_mysql_username
   db.password=your_mysql_password
   ```

### 2. Build the Application

1. **Clone the repository**:
   ```bash
   git clone https://github.com/Alex-S07/MediNova.git
   cd MediNova
   ```

2. **Compile and package**:
   ```bash
   mvn clean compile
   mvn package
   ```

### 3. Run the Application

Execute the application using Maven:
```bash
mvn exec:java -Dexec.mainClass="com.medinova.MediNovaApplication"
```

Or run the JAR file directly:
```bash
java -cp target/hospital-management-system-1.0-SNAPSHOT.jar com.medinova.MediNovaApplication
```

## Default Login Credentials

The system comes with a default administrator account:
- **Username**: `admin`
- **Password**: `admin123`

> ⚠️ **Important**: Change the default password immediately after first login for security purposes.

## User Roles & Permissions

### 👑 Administrator
- Full system access
- User management (create, edit, delete users)
- System configuration
- Reports and audit logs
- All patient, doctor, appointment, billing, and pharmacy operations

### 👨‍⚕️ Doctor
- View and manage assigned appointments
- Access patient medical records
- Update appointment status and notes
- View prescription history

### 👩‍⚕️ Nurse
- Patient management
- Appointment scheduling and management
- Medicine inventory access
- Vital signs recording

### 📋 Receptionist
- Patient registration and management
- Appointment scheduling
- Billing and payment processing
- Basic reporting

### 💊 Pharmacist
- Pharmacy inventory management
- Medicine dispensing
- Stock management and ordering
- Expiry tracking

## Project Structure

```
src/
├── main/
│   ├── java/com/medinova/
│   │   ├── config/          # Database configuration
│   │   ├── controller/      # Business logic controllers
│   │   ├── model/          # Data models (User, Patient, Doctor, etc.)
│   │   ├── util/           # Utility classes (Authentication, Password)
│   │   ├── view/           # UI components (Swing frames and panels)
│   │   └── MediNovaApplication.java  # Main application class
│   └── resources/
│       ├── database.properties      # Database configuration
│       └── schema.sql              # Database schema
└── test/
    └── java/com/medinova/          # Unit tests
```

## Key Features Implementation

### 🔄 Concurrency Control
The appointment scheduling system implements row-level locking to prevent double-booking:
```java
// Transaction-based appointment creation with conflict detection
String checkSql = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_date = ? AND appointment_time = ? FOR UPDATE";
```

### 🔐 Security Features
- **Password Hashing**: Using BCrypt with configurable rounds
- **Session Management**: Secure session handling with timeout
- **Role-Based Access**: Dynamic UI and functionality based on user roles

### 📊 Database Design
- **Normalized Schema**: Properly normalized tables with foreign key constraints
- **Indexing**: Strategic indexes for performance optimization
- **Audit Trail**: Complete logging of all database changes

## Development

### Adding New Features
1. **Model**: Create new model classes in `src/main/java/com/medinova/model/`
2. **Controller**: Implement business logic in `src/main/java/com/medinova/controller/`
3. **View**: Add UI components in `src/main/java/com/medinova/view/`
4. **Database**: Update schema in `src/main/resources/schema.sql`

### Testing
Run tests using Maven:
```bash
mvn test
```

### Building for Distribution
Create a distributable JAR with dependencies:
```bash
mvn clean package
```

## Troubleshooting

### Common Issues

1. **Database Connection Failed**
   - Verify MySQL is running
   - Check database credentials in `database.properties`
   - Ensure the database `medinova_hospital` exists

2. **Login Issues**
   - Use default credentials: `admin` / `admin123`
   - Check if users table has been created and populated

3. **Build Errors**
   - Ensure Java 11+ is installed
   - Verify Maven is properly configured
   - Check all dependencies are available

### System Requirements
- **Memory**: Minimum 512MB RAM, Recommended 1GB+
- **Storage**: 100MB for application, additional space for database
- **Java**: OpenJDK or Oracle JDK 11+
- **Database**: MySQL 8.0+ (can be configured for other databases)

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

For support and questions:
- Open an issue on GitHub
- Contact the development team
- Check the documentation in the `docs/` folder

---

**MediNova Hospital Management System** - Empowering healthcare with technology! 🏥✨

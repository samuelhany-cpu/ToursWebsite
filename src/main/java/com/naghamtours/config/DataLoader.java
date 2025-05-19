package com.naghamtours.config;

import com.naghamtours.entity.Employee;
import com.naghamtours.repository.EmployeeRepository;
import com.naghamtours.entity.*;
import com.naghamtours.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

@Component
public class DataLoader implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    private final EmployeeRepository employeeRepository;
    private final ClientRepository clientRepository;
    private final PackageRepository packageRepository;
    private final BookingRepository bookingRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(
            EmployeeRepository employeeRepository,
            ClientRepository clientRepository,
            PackageRepository packageRepository,
            BookingRepository bookingRepository,
            PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.clientRepository = clientRepository;
        this.packageRepository = packageRepository;
        this.bookingRepository = bookingRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        try {
            // Check if data already exists
            if (employeeRepository.count() > 0) {
                logger.info("Data already exists, skipping data loading");
                return;
            }

            logger.info("Starting to load initial data...");

            // Create admin employee
            Employee admin = createAdminEmployee();
            employeeRepository.save(admin);
            logger.info("Admin user created successfully");

            // Create regular employee
            Employee employee = createRegularEmployee();
            employeeRepository.save(employee);
            logger.info("Regular employee created successfully");

            // Create client
            Client client = createClient();
            clientRepository.save(client);
            logger.info("Client created successfully");

            // Create sample packages
            com.naghamtours.entity.Package package1 = createPackage1();
            packageRepository.save(package1);
            logger.info("Package 1 created successfully");

            com.naghamtours.entity.Package package2 = createPackage2();
            packageRepository.save(package2);
            logger.info("Package 2 created successfully");

            // Create sample booking
            Booking booking = createBooking(client, package1);
            bookingRepository.save(booking);
            logger.info("Sample booking created successfully");

            logger.info("Initial data loading completed successfully");
        } catch (Exception e) {
            logger.error("Error loading initial data: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to load initial data", e);
        }
    }

    private Employee createAdminEmployee() {
        Employee admin = new Employee();
        admin.setEmpName("admin");
        admin.setEmpPassword(passwordEncoder.encode("admin123"));
        admin.setEmpEmail("admin@naghamtours.com");
        admin.setFirstName("Admin");
        admin.setLastName("User");
        admin.setJobTitle("System Administrator");
        admin.setDepartment("IT");
        admin.setEnabled(true);
        admin.setRole(Employee.Role.ROLE_ADMIN);
        return admin;
    }

    private Employee createRegularEmployee() {
        Employee employee = new Employee();
        employee.setEmpName("employee");
        employee.setEmpPassword(passwordEncoder.encode("employee123"));
        employee.setEmpEmail("employee@naghamtours.com");
        employee.setFirstName("Regular");
        employee.setLastName("Employee");
        employee.setJobTitle("Tour Guide");
        employee.setDepartment("Operations");
        employee.setEnabled(true);
        employee.setRole(Employee.Role.ROLE_EMPLOYEE);
        return employee;
    }

    private Client createClient() {
        Client client = new Client();
        client.setClientName("client");
        client.setClientPassword(passwordEncoder.encode("client123"));
        client.setClientEmail("client@example.com");
        client.setFirstName("John");
        client.setLastName("Doe");
        client.setClientType(Client.ClientType.REGULAR);
        client.setEnabled(true);
        return client;
    }

    private com.naghamtours.entity.Package createPackage1() {
        com.naghamtours.entity.Package package1 = new com.naghamtours.entity.Package();
        package1.setName("Summer Beach Tour");
        package1.setDescription("Enjoy a week at the beach");
        package1.setDestination("Beach Resort");
        package1.setPrice(1000.0);
        package1.setDuration(7);
        package1.setMaxParticipants(20);
        
        LocalDateTime startDate1 = LocalDateTime.now().plusDays(30);
        LocalDateTime endDate1 = startDate1.plusDays(7);
        validateDateRange(startDate1, endDate1);
        
        package1.setStartDate(startDate1);
        package1.setEndDate(endDate1);
        package1.setLocation("Beach Resort");
        package1.setEnabled(true);
        return package1;
    }

    private com.naghamtours.entity.Package createPackage2() {
        com.naghamtours.entity.Package package2 = new com.naghamtours.entity.Package();
        package2.setName("Mountain Adventure");
        package2.setDescription("Experience the thrill of mountain climbing");
        package2.setDestination("Mountain Range");
        package2.setPrice(1500.0);
        package2.setDuration(5);
        package2.setMaxParticipants(15);
        
        LocalDateTime startDate2 = LocalDateTime.now().plusDays(45);
        LocalDateTime endDate2 = startDate2.plusDays(5);
        validateDateRange(startDate2, endDate2);
        
        package2.setStartDate(startDate2);
        package2.setEndDate(endDate2);
        package2.setLocation("Mountain Range");
        package2.setEnabled(true);
        return package2;
    }

    private Booking createBooking(Client client, com.naghamtours.entity.Package package1) {
        Booking booking = new Booking();
        booking.setClient(client);
        booking.setPackageEntity(package1);
        booking.setBookingDate(LocalDateTime.now());
        booking.setParticipants(2);
        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        return booking;
    }

    private void validateDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date cannot be null");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
        if (startDate.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Start date must be in the future");
        }
    }
} 
package com.naghamtours.config;

import com.naghamtours.entity.*;
import com.naghamtours.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

@Component
public class DataLoader implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private PackageRepository packageRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        try {
            // Check if data already exists
            if (employeeRepository.count() > 0) {
                logger.info("Data already exists, skipping data loading");
                return;
            }

            logger.info("Starting to load initial data...");

            // Create admin employee
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
            if (employeeRepository.existsByEmpName(admin.getEmpName())) {
                logger.warn("Admin user already exists, skipping creation");
            } else {
                employeeRepository.save(admin);
                logger.info("Admin user created successfully");
            }

            // Create regular employee
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
            if (employeeRepository.existsByEmpName(employee.getEmpName())) {
                logger.warn("Regular employee already exists, skipping creation");
            } else {
                employeeRepository.save(employee);
                logger.info("Regular employee created successfully");
            }

            // Create client
            Client client = new Client();
            client.setClientName("client");
            client.setClientPassword(passwordEncoder.encode("client123"));
            client.setClientEmail("client@example.com");
            client.setFirstName("John");
            client.setLastName("Doe");
            client.setClientType(Client.ClientType.REGULAR);
            client.setEnabled(true);
            if (clientRepository.existsByClientName(client.getClientName())) {
                logger.warn("Client already exists, skipping creation");
            } else {
                clientRepository.save(client);
                logger.info("Client created successfully");
            }

            // Create sample packages
            com.naghamtours.entity.Package package1 = new com.naghamtours.entity.Package();
            package1.setPackageName("Summer Beach Tour");
            package1.setDescription("Enjoy a week at the beach");
            package1.setPrice(1000.0);
            package1.setDuration(7);
            package1.setMaxParticipants(20);
            LocalDateTime startDate1 = LocalDateTime.now().plusDays(30);
            LocalDateTime endDate1 = LocalDateTime.now().plusDays(37);
            if (startDate1.isAfter(endDate1)) {
                logger.error("Invalid date range for package 1");
                return;
            }
            package1.setStartDate(startDate1);
            package1.setEndDate(endDate1);
            package1.setLocation("Beach Resort");
            package1.setEnabled(true);
            packageRepository.save(package1);
            logger.info("Package 1 created successfully");

            com.naghamtours.entity.Package package2 = new com.naghamtours.entity.Package();
            package2.setPackageName("Mountain Adventure");
            package2.setDescription("Hiking and camping in the mountains");
            package2.setPrice(800.0);
            package2.setDuration(5);
            package2.setMaxParticipants(15);
            LocalDateTime startDate2 = LocalDateTime.now().plusDays(45);
            LocalDateTime endDate2 = LocalDateTime.now().plusDays(50);
            if (startDate2.isAfter(endDate2)) {
                logger.error("Invalid date range for package 2");
                return;
            }
            package2.setStartDate(startDate2);
            package2.setEndDate(endDate2);
            package2.setLocation("Mountain Range");
            package2.setEnabled(true);
            packageRepository.save(package2);
            logger.info("Package 2 created successfully");

            // Create sample booking
            Booking booking = new Booking();
            booking.setClient(client);
            booking.setPackageEntity(package1);
            booking.setBookingDate(LocalDateTime.now());
            booking.setParticipants(2);
            if (booking.getParticipants() > package1.getMaxParticipants()) {
                logger.error("Invalid number of participants for booking");
                return;
            }
            booking.setStatus(Booking.BookingStatus.CONFIRMED);
            bookingRepository.save(booking);
            logger.info("Sample booking created successfully");

            logger.info("Initial data loading completed successfully");
        } catch (Exception e) {
            logger.error("Error loading initial data: " + e.getMessage(), e);
        }
    }
} 
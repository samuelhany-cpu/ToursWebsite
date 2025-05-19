package com.naghamtours.controller;

import com.naghamtours.entity.Employee;
import com.naghamtours.entity.Client;
import com.naghamtours.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/employees")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.ok(userService.getAllEmployees());
    }

    @GetMapping("/clients")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<List<Client>> getAllClients() {
        return ResponseEntity.ok(userService.getAllClients());
    }

    @GetMapping("/employees/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        return userService.getEmployeeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/clients/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<Client> getClientById(@PathVariable Long id) {
        return userService.getClientById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/employees")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
        if (employee == null || employee.getEmpName() == null || employee.getEmpPassword() == null) {
            return ResponseEntity.badRequest().build();
        }
        if (userService.existsByUsername(employee.getEmpName())) {
            return ResponseEntity.badRequest().build();
        }
        employee.setEmpPassword(passwordEncoder.encode(employee.getEmpPassword()));
        return ResponseEntity.ok(userService.saveEmployee(employee));
    }

    @PostMapping("/clients")
    public ResponseEntity<Client> createClient(@RequestBody Client client) {
        if (client == null || client.getClientName() == null || client.getClientPassword() == null) {
            return ResponseEntity.badRequest().build();
        }
        if (userService.existsByUsername(client.getClientName())) {
            return ResponseEntity.badRequest().build();
        }
        client.setClientPassword(passwordEncoder.encode(client.getClientPassword()));
        return ResponseEntity.ok(userService.saveClient(client));
    }

    @PutMapping("/employees/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        if (employee == null || employee.getEmpName() == null) {
            return ResponseEntity.badRequest().build();
        }
        return userService.getEmployeeById(id)
                .map(existingEmployee -> {
                    employee.setId(id);
                    if (employee.getEmpPassword() != null && !employee.getEmpPassword().isEmpty()) {
                        employee.setEmpPassword(passwordEncoder.encode(employee.getEmpPassword()));
                    } else {
                        employee.setEmpPassword(existingEmployee.getEmpPassword());
                    }
                    return ResponseEntity.ok(userService.saveEmployee(employee));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/clients/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<Client> updateClient(@PathVariable Long id, @RequestBody Client client) {
        if (client == null || client.getClientName() == null) {
            return ResponseEntity.badRequest().build();
        }
        return userService.getClientById(id)
                .map(existingClient -> {
                    client.setId(id);
                    if (client.getClientPassword() != null && !client.getClientPassword().isEmpty()) {
                        client.setClientPassword(passwordEncoder.encode(client.getClientPassword()));
                    } else {
                        client.setClientPassword(existingClient.getClientPassword());
                    }
                    return ResponseEntity.ok(userService.saveClient(client));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/employees/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        return userService.getEmployeeById(id)
                .map(employee -> {
                    userService.deleteEmployee(id);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/clients/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        return userService.getClientById(id)
                .map(client -> {
                    userService.deleteClient(id);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
} 
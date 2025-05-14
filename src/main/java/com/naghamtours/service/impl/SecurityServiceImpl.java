package com.naghamtours.service.impl;

import com.naghamtours.entity.Employee;
import com.naghamtours.entity.Client;
import com.naghamtours.repository.EmployeeRepository;
import com.naghamtours.repository.ClientRepository;
import com.naghamtours.service.SecurityService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityServiceImpl implements SecurityService {

    private final EmployeeRepository employeeRepository;
    private final ClientRepository clientRepository;

    public SecurityServiceImpl(EmployeeRepository employeeRepository, ClientRepository clientRepository) {
        this.employeeRepository = employeeRepository;
        this.clientRepository = clientRepository;
    }

    @Override
    public Employee getCurrentEmployee() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        return employeeRepository.findByEmpName(auth.getName()).orElse(null);
    }

    @Override
    public Client getCurrentClient() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        return clientRepository.findByClientName(auth.getName()).orElse(null);
    }

    @Override
    public boolean isAdmin() {
        Employee employee = getCurrentEmployee();
        return employee != null && Employee.Role.ROLE_ADMIN.equals(employee.getRole());
    }

    @Override
    public boolean isEmployee() {
        Employee employee = getCurrentEmployee();
        return employee != null && Employee.Role.ROLE_EMPLOYEE.equals(employee.getRole());
    }

    @Override
    public boolean isClient() {
        return getCurrentClient() != null;
    }
} 
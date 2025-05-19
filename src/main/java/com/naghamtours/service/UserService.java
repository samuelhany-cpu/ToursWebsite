package com.naghamtours.service;

import com.naghamtours.entity.User;
import com.naghamtours.entity.Client;
import com.naghamtours.entity.Employee;
import java.util.List;
import java.util.Optional;

public interface UserService {
    User registerUser(User user);
    User findByUsername(String username);
    User findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    User saveUser(User user);
    
    // Client management methods
    List<Client> getAllClients();
    Optional<Client> getClientById(Long id);
    Client saveClient(Client client);
    void deleteClient(Long id);
    
    // Employee management methods
    List<Employee> getAllEmployees();
    Optional<Employee> getEmployeeById(Long id);
    Employee saveEmployee(Employee employee);
    void deleteEmployee(Long id);
} 
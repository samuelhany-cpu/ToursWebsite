package com.naghamtours.service;

import com.naghamtours.entity.Employee;
import com.naghamtours.entity.Client;
import java.util.List;
import java.util.Optional;

public interface UserService {
    List<Employee> getAllEmployees();
    List<Client> getAllClients();
    Optional<Employee> getEmployeeById(Long id);
    Optional<Client> getClientById(Long id);
    Employee saveEmployee(Employee employee);
    Client saveClient(Client client);
    void deleteEmployee(Long id);
    void deleteClient(Long id);
    Optional<Employee> findByUsername(String username);
    Optional<Client> findClientByUsername(String username);
    boolean existsByUsername(String username);
} 
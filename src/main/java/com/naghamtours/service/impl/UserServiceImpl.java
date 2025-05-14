package com.naghamtours.service.impl;

import com.naghamtours.entity.Employee;
import com.naghamtours.entity.Client;
import com.naghamtours.repository.EmployeeRepository;
import com.naghamtours.repository.ClientRepository;
import com.naghamtours.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    @Override
    public Optional<Employee> getEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }

    @Override
    public Optional<Client> getClientById(Long id) {
        return clientRepository.findById(id);
    }

    @Override
    public Employee saveEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    @Override
    public Client saveClient(Client client) {
        return clientRepository.save(client);
    }

    @Override
    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }

    @Override
    public void deleteClient(Long id) {
        clientRepository.deleteById(id);
    }

    @Override
    public Optional<Employee> findByUsername(String username) {
        return employeeRepository.findByEmpName(username);
    }

    @Override
    public Optional<Client> findClientByUsername(String username) {
        return clientRepository.findByClientName(username);
    }

    @Override
    public boolean existsByUsername(String username) {
        return employeeRepository.existsByEmpName(username) || 
               clientRepository.existsByClientName(username);
    }
} 
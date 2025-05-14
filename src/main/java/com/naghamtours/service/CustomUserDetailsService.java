package com.naghamtours.service;

import com.naghamtours.entity.Employee;
import com.naghamtours.entity.Client;
import com.naghamtours.repository.EmployeeRepository;
import com.naghamtours.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // First try to find an employee
        Employee employee = employeeRepository.findByEmpName(username)
                .orElse(null);

        if (employee != null) {
            return new User(
                employee.getEmpName(),
                employee.getEmpPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + employee.getRole().name()))
            );
        }

        // If not an employee, try to find a client
        Client client = clientRepository.findByClientName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return new User(
            client.getClientName(),
            client.getClientPassword(),
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_CLIENT"))
        );
    }
} 
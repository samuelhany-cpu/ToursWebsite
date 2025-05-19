package com.naghamtours.service;

import com.naghamtours.entity.Employee;
import com.naghamtours.entity.Client;
import com.naghamtours.entity.Admin;
import com.naghamtours.repository.EmployeeRepository;
import com.naghamtours.repository.ClientRepository;
import com.naghamtours.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final EmployeeRepository employeeRepository;
    private final ClientRepository clientRepository;
    private final AdminRepository adminRepository;

    @Autowired
    public CustomUserDetailsService(EmployeeRepository employeeRepository, ClientRepository clientRepository, AdminRepository adminRepository) {
        this.employeeRepository = employeeRepository;
        this.clientRepository = clientRepository;
        this.adminRepository = adminRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Try admin first
        Admin admin = adminRepository.findByUsername(username).orElse(null);
        if (admin != null) {
            // Update last login timestamp
            admin.setLastLogin(LocalDateTime.now());
            adminRepository.save(admin);
            
            return new User(
                admin.getUsername(),
                admin.getPassword(),
                admin.isEnabled(),
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
            );
        }

        // Try employee
        Employee employee = employeeRepository.findByEmpName(username).orElse(null);
        if (employee != null) {
            return new User(
                employee.getEmpName(),
                employee.getEmpPassword(),
                employee.isEnabled(),
                true, true, true,
                Collections.singletonList(new SimpleGrantedAuthority(employee.getRole().name()))
            );
        }

        // Try client
        Client client = clientRepository.findByClientName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return new User(
            client.getClientName(),
            client.getClientPassword(),
            client.isEnabled(),
            true, true, true,
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_CLIENT"))
        );
    }

    private UserDetails buildUserFromEmployee(Employee employee) {
        return new User(
                employee.getEmpName(),
                employee.getEmpPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(employee.getRole().name()))
        );
    }

    private UserDetails buildUserFromClient(Client client) {
        return new User(
                client.getClientName(),
                client.getClientPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_CLIENT"))
        );
    }
}
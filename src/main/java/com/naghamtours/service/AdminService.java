package com.naghamtours.service;

import com.naghamtours.entity.Admin;
import java.util.List;
import java.util.Optional;

public interface AdminService {
    Admin findByUsername(String username);
    Admin save(Admin admin);
    List<Admin> findAll();
    Optional<Admin> findById(Long id);
    void deleteById(Long id);
} 
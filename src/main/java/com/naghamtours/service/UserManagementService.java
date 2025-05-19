package com.naghamtours.service;

import com.naghamtours.entity.User;

public interface UserManagementService {
    User registerUser(User user);
    User findByUsername(String username);
    User findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
} 
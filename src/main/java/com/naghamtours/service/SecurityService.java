package com.naghamtours.service;

import com.naghamtours.entity.Employee;
import com.naghamtours.entity.Client;

public interface SecurityService {
    Employee getCurrentEmployee();
    Client getCurrentClient();
    boolean isAdmin();
    boolean isEmployee();
    boolean isClient();
} 
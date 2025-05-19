package com.naghamtours.repository;

import com.naghamtours.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmpName(String empName);
    Optional<Employee> findByEmpEmail(String empEmail);
    boolean existsByEmpName(String empName);
} 
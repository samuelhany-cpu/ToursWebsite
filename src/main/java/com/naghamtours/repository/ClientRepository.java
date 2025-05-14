package com.naghamtours.repository;

import com.naghamtours.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByClientName(String clientName);
    boolean existsByClientName(String clientName);
    Optional<Client> findByClientEmail(String email);
} 
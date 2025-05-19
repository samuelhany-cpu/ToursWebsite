package com.naghamtours.repository;

import com.naghamtours.entity.ClientTransactionPayment;
import com.naghamtours.entity.Reserve;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientTransactionPaymentRepository extends JpaRepository<ClientTransactionPayment, Integer> {
    void deleteByTransaction(Reserve transaction);
} 
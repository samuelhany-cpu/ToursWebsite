package com.naghamtours.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "client_transaction_payments")
public class ClientTransactionPayment {
    @Id
    @Column(name = "PAYMENT_ID", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EMP_ID")
    private Employee emp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TRANSACTION_ID")
    private Reserve transaction;

    @Column(name = "PAYMENT_DATE")
    private LocalDate paymentDate;

    @Size(max = 45)
    @Column(name = "PAYMENT_METHOD", length = 45)
    private String paymentMethod;

    @Column(name = "AMOUNT_PAID")
    private Float amountPaid;

    @Size(max = 45)
    @Column(name = "PAYMENT_STATUS", length = 45)
    private String paymentStatus;

}
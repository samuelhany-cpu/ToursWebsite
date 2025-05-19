package com.naghamtours.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "reserve")
public class Reserve {
    @Id
    @Column(name = "TRANSACTION_ID", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EMP_ID")
    private Employee emp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TRANSPORT_ID")
    private Transport transport;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PACKAGE_ID")
    private Package packageEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CLIENT_ID")
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PAYMENT_ID")
    private ClientTransactionPayment payment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POLICY_ID")
    private Policy policy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OPTION_ID")
    private Option option;

    @Column(name = "CANCELATION_DATE")
    private LocalDate cancelationDate;

    @Column(name = "BOOKING_DATE")
    private LocalDate bookingDate;

    @Column(name = "REFUND")
    private Float refund;

    // Custom getter and setter for payment to handle null values
    public ClientTransactionPayment getPayment() {
        return payment;
    }

    public void setPayment(ClientTransactionPayment payment) {
        this.payment = payment;
    }
}
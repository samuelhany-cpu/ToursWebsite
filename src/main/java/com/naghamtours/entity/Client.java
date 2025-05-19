package com.naghamtours.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "CLIENTS")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CLIENT_ID")
    private Long id;

    @NotBlank
    @Column(name = "CLIENT_NAME", unique = true, nullable = false)
    private String clientName;

    @NotBlank
    @Column(name = "CLIENT_PASSWORD", nullable = false)
    private String clientPassword;

    @Email
    @Column(name = "CLIENT_EMAIL", unique = true)
    private String clientEmail;

    @Column(name = "CLIENT_FIRST_NAME")
    private String firstName;

    @Column(name = "CLIENT_LAST_NAME")
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(name = "CLIENT_TYPE", nullable = false)
    private ClientType clientType;

    @Column(name = "CLIENT_ENABLED")
    private boolean enabled = true;

    @Column(name = "CLIENT_PHONE")
    private String phone;

    @Column(name = "CLIENT_ADDRESS")
    private String address;

    public enum ClientType {
        REGULAR, VIP, CORPORATE
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientPassword() {
        return clientPassword;
    }

    public void setClientPassword(String clientPassword) {
        this.clientPassword = clientPassword;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public ClientType getClientType() {
        return clientType;
    }

    public void setClientType(ClientType clientType) {
        this.clientType = clientType;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
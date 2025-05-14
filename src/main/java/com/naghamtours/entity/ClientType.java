package com.naghamtours.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "client_type")
public class ClientType {
    @Id
    @Column(name = "CLIENT_TYPE_ID", nullable = false)
    private Integer id;

    @Size(max = 45)
    @Column(name = "CLIENT_TYPE_NAME", length = 45)
    private String clientTypeName;

}
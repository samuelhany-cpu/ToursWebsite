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
@Table(name = "emp_license")
public class EmpLicense {
    @Id
    @Column(name = "LICENSE_ID", nullable = false)
    private Integer id;

    @Size(max = 45)
    @Column(name = "LICENSE_TYPE", length = 45)
    private String licenseType;

}
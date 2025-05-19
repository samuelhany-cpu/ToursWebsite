package com.naghamtours.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "departments")
public class Department {
    @Id
    @Column(name = "DEPT_ID", nullable = false)
    private Integer id;

    @Size(max = 255)
    @NotNull
    @Column(name = "DEPT_NAME", nullable = false)
    private String deptName;

    @Column(name = "EMP_ID")
    private Integer empId;

}
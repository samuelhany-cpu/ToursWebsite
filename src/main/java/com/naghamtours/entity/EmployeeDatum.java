package com.naghamtours.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "employee_data")
public class EmployeeDatum {
    @Id
    @Column(name = "EMP_ID", nullable = false)
    private Integer id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "EMP_ID", nullable = false)
    private Employee employee;

    @Lob
    @Column(name = "START_DATE")
    private String startDate;

    @Column(name = "SALARY")
    private Integer salary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "JOB_ID")
    private EmpJob job;

}
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
@Table(name = "emp_job")
public class EmpJob {
    @Id
    @Column(name = "JOB_ID", nullable = false)
    private Integer id;

    @Size(max = 50)
    @Column(name = "JOB_NAME", length = 50)
    private String jobName;

}
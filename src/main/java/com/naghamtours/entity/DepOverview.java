package com.naghamtours.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "dep_overview")
public class DepOverview {
    @EmbeddedId
    private DepOverviewId id;

    @MapsId("deptId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "DEPT_ID", nullable = false)
    private Department dept;

    @Column(name = "NO_OF_EMPLOYEES")
    private Integer noOfEmployees;

}
package com.naghamtours.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "policy")
public class Policy {
    @Id
    @Column(name = "POILICY_ID", nullable = false)
    private Integer id;

    @Column(name = "DAYS_NUM")
    private Integer daysNum;

    @Column(name = "PERCENTAGE")
    private Integer percentage;

}
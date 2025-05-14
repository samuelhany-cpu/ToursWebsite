package com.naghamtours.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class DepOverviewId implements Serializable {
    private static final long serialVersionUID = -6022715718690755015L;
    @NotNull
    @Column(name = "DEPT_ID", nullable = false)
    private Integer deptId;

    @NotNull
    @Column(name = "START_DATE", nullable = false)
    private LocalDate startDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        DepOverviewId entity = (DepOverviewId) o;
        return Objects.equals(this.deptId, entity.deptId) &&
                Objects.equals(this.startDate, entity.startDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deptId, startDate);
    }

}
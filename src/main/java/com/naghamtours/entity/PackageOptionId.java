package com.naghamtours.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class PackageOptionId implements Serializable {
    private static final long serialVersionUID = 8970480482646474205L;
    @NotNull
    @Column(name = "OPTION_ID", nullable = false)
    private Integer optionId;

    @NotNull
    @Column(name = "PACKAGE_ID", nullable = false)
    private Integer packageId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        PackageOptionId entity = (PackageOptionId) o;
        return Objects.equals(this.packageId, entity.packageId) &&
                Objects.equals(this.optionId, entity.optionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(packageId, optionId);
    }

}
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
public class CancelId implements Serializable {
    private static final long serialVersionUID = -3134523510641959264L;
    @NotNull
    @Column(name = "CLIENT_ID", nullable = false)
    private Integer clientId;

    @NotNull
    @Column(name = "PACKAGE_ID", nullable = false)
    private Integer packageId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        CancelId entity = (CancelId) o;
        return Objects.equals(this.clientId, entity.clientId) &&
                Objects.equals(this.packageId, entity.packageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientId, packageId);
    }

}
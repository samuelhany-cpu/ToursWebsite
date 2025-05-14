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
public class ClientPhoneNoId implements Serializable {
    private static final long serialVersionUID = 96943327156329617L;
    @NotNull
    @Column(name = "CLIENT_ID", nullable = false)
    private Integer clientId;

    @NotNull
    @Column(name = "CLIENT_PHONE_NUMBER", nullable = false)
    private Long clientPhoneNumber;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ClientPhoneNoId entity = (ClientPhoneNoId) o;
        return Objects.equals(this.clientId, entity.clientId) &&
                Objects.equals(this.clientPhoneNumber, entity.clientPhoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientId, clientPhoneNumber);
    }

}
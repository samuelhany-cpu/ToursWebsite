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
public class ClientLanguageId implements Serializable {
    private static final long serialVersionUID = -8224831279755303494L;
    @NotNull
    @Column(name = "CLIENT_ID", nullable = false)
    private Integer clientId;

    @NotNull
    @Column(name = "LANGUAGE_ID", nullable = false)
    private Integer languageId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ClientLanguageId entity = (ClientLanguageId) o;
        return Objects.equals(this.clientId, entity.clientId) &&
                Objects.equals(this.languageId, entity.languageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientId, languageId);
    }

}
package com.naghamtours.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "language")
public class Language {
    @Id
    @Column(name = "LANGUAGE_ID", nullable = false)
    private Integer id;

    @Lob
    @Column(name = "LANGUAGE_NAME")
    private String languageName;

}
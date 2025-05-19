package com.naghamtours.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "event_publication")
public class EventPublication {
    @Id
    @Size(max = 16)
    @Column(name = "id", nullable = false, length = 16)
    private String id;

    @Column(name = "completion_date")
    private Instant completionDate;

    @Size(max = 255)
    @Column(name = "event_type")
    private String eventType;

    @Size(max = 255)
    @Column(name = "listener_id")
    private String listenerId;

    @Column(name = "publication_date")
    private Instant publicationDate;

    @Size(max = 255)
    @Column(name = "serialized_event")
    private String serializedEvent;

}
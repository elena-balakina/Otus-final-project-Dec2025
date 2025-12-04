package org.otus.finalProject.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntity {
    @CreationTimestamp
    @Column(name = "created_date_time", nullable = false, updatable = false)
    private Instant createdDateTime;

    @UpdateTimestamp
    @Column(name = "updated_date_time")
    private Instant updatedDateTime;
}

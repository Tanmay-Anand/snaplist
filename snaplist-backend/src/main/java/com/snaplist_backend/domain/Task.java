package com.snaplist_backend.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="tasks",
        indexes = {
            @Index(name = "idx_task_due_date", columnList= "due_date"),
            @Index(name = "idx_task_priority", columnList = "priority"),
            @Index(name = "idx_task_status", columnList = "status")
        })
public class Task {

    public enum Status {
        PENDING,
        DONE
    }

    public enum Priority {
        LOW,
        MEDIUM,
        HIGH
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank
    @Column(nullable = false)
    private String text;

    @Enumerated(EnumType.STRING)
    private Status status=Status.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private Priority priority =Priority.MEDIUM;

    @Column(name= "due_date")
    private LocalDate dueDate;

    @Column(name="created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }
}

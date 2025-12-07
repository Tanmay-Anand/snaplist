package com.snaplist_backend.dto;

import com.snaplist_backend.domain.Task;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;

@Data
public class TaskResponse {

    private Long id;
    private String text;

    private Task.Status status;
    private Task.Priority priority;

    private LocalDate dueDate;

    private Instant createdAt;
    private Instant updatedAt;
}

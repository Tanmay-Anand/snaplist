package com.snaplist_backend.dto;

import com.snaplist_backend.domain.Task;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TaskRequest {

    @NotBlank(message = "Task text cannot be empty")
    @Size(max = 300, message = "Task text cannot exceed 300 characters")
    private String text;

    private Task.Status status;
    private Task.Priority priority;

    @FutureOrPresent(message = "Due date cannot be in the past")
    private LocalDate dueDate;
}

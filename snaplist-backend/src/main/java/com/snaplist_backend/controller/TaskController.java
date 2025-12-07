package com.snaplist_backend.controller;

import com.snaplist_backend.domain.Task;
import com.snaplist_backend.dto.TaskRequest;
import com.snaplist_backend.dto.TaskResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.snaplist_backend.service.TaskService;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    //Delegating the real work to TaskService.
    private final TaskService taskService;

    @PostMapping //A POST to /api/tasks sends a JSON body with task details.
    public ResponseEntity<TaskResponse> create(@Valid @RequestBody TaskRequest req) {
        TaskResponse resp = taskService.createTask(req); //The controller hands the request object to taskService.createTask(req).
        return ResponseEntity.status(HttpStatus.CREATED).body(resp); //Whatever the service returns gets wrapped in a 201 Created response.
        //This endpoint is simple and clean because all business logic lives in the service layer.
    }

    @GetMapping
    public ResponseEntity<Page<TaskResponse>> list(
            //@RequestParam makes each filter optional.
            //@DateTimeFormat tells Spring how to parse dates (ISO means YYYY-MM-DD).
            //@PageableDefault(size = 20) sets default pagination if the client doesn't specify page or size.
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "status", required = false) Task.Status status,
            @RequestParam(value = "priority", required = false) Task.Priority priority,
            @RequestParam(value = "dueBefore", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueBefore,
            @RequestParam(value = "dueAfter", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueAfter,
            @PageableDefault(size = 20) Pageable pageable) {

        //Once all filters are collected, the controller calls:
        Page<TaskResponse> page = taskService.listTasks(q, status, priority, dueBefore, dueAfter, pageable);
        //returns the page.
        return ResponseEntity.ok(page);
    }

    //GET a single task
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> get(@PathVariable Long id) {
        TaskResponse resp = taskService.getTask(id);
        return ResponseEntity.ok(resp);
    }

    //UPDATE a task
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> update(@PathVariable Long id, @Valid @RequestBody TaskRequest req) {
        TaskResponse resp = taskService.updateTask(id, req);
        return ResponseEntity.ok(resp);
    }

    //DELETE a task
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    //MARK a task as complete
    @PostMapping("/{id}/complete")
    public ResponseEntity<TaskResponse> complete(@PathVariable Long id) {
        TaskResponse resp = taskService.markCompleted(id);
        return ResponseEntity.ok(resp);
    }
    //The idea is to have a small, dedicated endpoint to change the task’s status to completed
    // —> instead of forcing clients to send a full update request.
}
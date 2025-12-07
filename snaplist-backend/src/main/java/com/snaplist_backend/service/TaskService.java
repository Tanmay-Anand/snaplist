package com.snaplist_backend.service;


import com.snaplist_backend.domain.Task;
import com.snaplist_backend.domain.User;
import com.snaplist_backend.dto.TaskRequest;
import com.snaplist_backend.dto.TaskResponse;
import com.snaplist_backend.exception.ResourceNotFoundException;
import com.snaplist_backend.mapper.TaskMapper;
import com.snaplist_backend.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;

    @Transactional
    public TaskResponse createTask(TaskRequest req) {
        User user = userService.getCurrentUser();
        Task t = TaskMapper.toEntity(req, user);
        Task saved = taskRepository.save(t);
        return TaskMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<TaskResponse> listTasks(String q,
                                        Task.Status status,
                                        Task.Priority priority,
                                        LocalDate dueBefore,
                                        LocalDate dueAfter,
                                        Pageable pageable) {
        Long userId = userService.getCurrentUserId();

        // prioritize explicit filters
        if (status != null) {
            return taskRepository.findByUserIdAndStatus(userId, status, pageable)
                    .map(TaskMapper::toResponse);
        }
        if (priority != null) {
            return taskRepository.findByUserIdAndPriority(userId, priority, pageable)
                    .map(TaskMapper::toResponse);
        }
        if (dueBefore != null && dueAfter != null) {
            // naive approach: filter in DB by range - add repository method if you want DB-level range
            // fallback: fetch user's tasks page and filter in memory (but for Phase 3 we assume small pages)
        }
        if (q != null && !q.isBlank()) {
            return taskRepository.searchByText(userId, q, pageable).map(TaskMapper::toResponse);
        }
        return taskRepository.findByUserId(userId, pageable).map(TaskMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public TaskResponse getTask(Long id) {
        Long userId = userService.getCurrentUserId();
        Task t = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task", id));
        if (!t.getUser().getId().equals(userId)) throw new ResourceNotFoundException("Task", id);
        return TaskMapper.toResponse(t);
    }

    @Transactional
    public TaskResponse updateTask(Long id, TaskRequest req) {
        Long userId = userService.getCurrentUserId();
        Task t = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task", id));
        if (!t.getUser().getId().equals(userId)) throw new ResourceNotFoundException("Task", id);
        TaskMapper.updateEntity(t, req);
        Task saved = taskRepository.save(t);
        return TaskMapper.toResponse(saved);
    }

    @Transactional
    public void deleteTask(Long id) {
        Long userId = userService.getCurrentUserId();
        Task t = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task", id));
        if (!t.getUser().getId().equals(userId)) throw new ResourceNotFoundException("Task", id);
        taskRepository.delete(t);
    }

    @Transactional
    public TaskResponse markCompleted(Long id) {
        Long userId = userService.getCurrentUserId();
        Task t = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task", id));
        if (!t.getUser().getId().equals(userId)) throw new ResourceNotFoundException("Task", id);
        t.setStatus(Task.Status.DONE);
        Task saved = taskRepository.save(t);
        return TaskMapper.toResponse(saved);
    }
}

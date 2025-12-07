package com.snaplist_backend.mapper;

import com.snaplist_backend.domain.User;
import com.snaplist_backend.domain.Task;
import com.snaplist_backend.dto.TaskRequest;
import com.snaplist_backend.dto.TaskResponse;

public class TaskMapper {
    public static Task toEntity(TaskRequest req, User user) {
        Task t= new Task();
        t.setUser(user);
        t.setText(req.getText());

        if(req.getStatus() != null) t.setStatus(req.getStatus());
        if(req.getPriority() != null) t.setPriority(req.getPriority());
        if (req.getDueDate() != null) t.setDueDate(req.getDueDate());

        return t;
    }

    public static void updateEntity(Task task, TaskRequest req) {
        task.setText(req.getText());

        if (req.getStatus() != null) task.setStatus(req.getStatus());
        if (req.getPriority() != null) task.setPriority(req.getPriority());
        if (req.getDueDate() != null) task.setDueDate(req.getDueDate());
    }

    public static TaskResponse toResponse(Task t) {
        TaskResponse r = new TaskResponse();

        r.setId(t.getId());
        r.setText(t.getText());
        r.setStatus(t.getStatus());
        r.setPriority(t.getPriority());
        r.setDueDate(t.getDueDate());
        r.setCreatedAt(t.getCreatedAt());
        r.setUpdatedAt(t.getUpdatedAt());
        return r;
    }
}

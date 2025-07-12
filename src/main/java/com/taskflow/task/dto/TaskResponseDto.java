package com.taskflow.task.dto;

import com.taskflow.task.enums.TaskPriority;
import com.taskflow.task.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponseDto {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDate dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean overdue;
    private boolean completed;

    // Constructor with minimal fields
    public TaskResponseDto(Long id, String title, String description, TaskStatus status, TaskPriority priority) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.completed = status == TaskStatus.COMPLETED;
    }
}
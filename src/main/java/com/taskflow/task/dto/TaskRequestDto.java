package com.taskflow.task.dto;

import com.taskflow.task.enums.TaskPriority;
import com.taskflow.task.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequestDto {
    @NotBlank(message = "Task title is required")
    @Size(min = 1, max = 255, message = "Task title must be between 1 and 255 characters")
    private String title;

    @Size(max = 1000, message = "Task description must not exceed 1000 characters")
    private String description;

    @NotNull(message = "Task status is required")
    private TaskStatus status;

    @NotNull(message = "Task priority is required")
    private TaskPriority priority;

    private LocalDate dueDate;
}
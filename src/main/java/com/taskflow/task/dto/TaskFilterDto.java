package com.taskflow.task.dto;

import com.taskflow.task.enums.TaskPriority;
import com.taskflow.task.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskFilterDto {
    private String search;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDate dueDateFrom;
    private LocalDate dueDateTo;
    private Boolean overdue;
    private String sortBy;
    private String sortDirection;
    private Integer page;
    private Integer size;

    // Default values
    public String getSortBy() {
        return sortBy != null ? sortBy : "createdAt";
    }

    public String getSortDirection() {
        return sortDirection != null ? sortDirection : "desc";
    }

    public Integer getPage() {
        return page != null ? page : 0;
    }

    public Integer getSize() {
        return size != null ? size : 20;
    }
}
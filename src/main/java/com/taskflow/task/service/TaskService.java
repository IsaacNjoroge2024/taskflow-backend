package com.taskflow.task.service;

import com.taskflow.task.dto.TaskFilterDto;
import com.taskflow.task.dto.TaskRequestDto;
import com.taskflow.task.dto.TaskResponseDto;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TaskService {

    TaskResponseDto createTask(TaskRequestDto taskRequestDto);

    TaskResponseDto getTaskById(Long id);

    TaskResponseDto updateTask(Long id, TaskRequestDto taskRequestDto);

    void deleteTask(Long id);

    Page<TaskResponseDto> getTasks(TaskFilterDto filterDto);

    TaskResponseDto toggleTaskCompletion(Long id);

    List<TaskResponseDto> getOverdueTasks();

    TaskStatisticsDto getTaskStatistics();

    @Getter
    class TaskStatisticsDto {
        // Getters
        private final long totalTasks;
        private final long pendingTasks;
        private final long completedTasks;
        private final long overdueTasks;
        private final long highPriorityTasks;

        public TaskStatisticsDto(long totalTasks, long pendingTasks, long completedTasks,
                                 long overdueTasks, long highPriorityTasks) {
            this.totalTasks = totalTasks;
            this.pendingTasks = pendingTasks;
            this.completedTasks = completedTasks;
            this.overdueTasks = overdueTasks;
            this.highPriorityTasks = highPriorityTasks;
        }

    }
}
package com.taskflow.task.service;

import com.taskflow.common.exception.TaskNotFoundException;
import com.taskflow.task.dto.TaskFilterDto;
import com.taskflow.task.dto.TaskRequestDto;
import com.taskflow.task.dto.TaskResponseDto;
import com.taskflow.task.entity.Task;
import com.taskflow.task.enums.TaskPriority;
import com.taskflow.task.enums.TaskStatus;
import com.taskflow.task.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of TaskService interface
 *
 * Provides the business logic for task management operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    @Override
    @Transactional
    public TaskResponseDto createTask(TaskRequestDto taskRequestDto) {
        log.info("Creating new task with title: {}", taskRequestDto.getTitle());

        Task task = new Task();
        mapDtoToEntity(taskRequestDto, task);

        Task savedTask = taskRepository.save(task);
        log.info("Task created successfully with ID: {}", savedTask.getId());

        return mapEntityToDto(savedTask);
    }

    @Override
    public TaskResponseDto getTaskById(Long id) {
        log.info("Fetching task with ID: {}", id);

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with ID: " + id));

        return mapEntityToDto(task);
    }

    @Override
    @Transactional
    public TaskResponseDto updateTask(Long id, TaskRequestDto taskRequestDto) {
        log.info("Updating task with ID: {}", id);

        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with ID: " + id));

        mapDtoToEntity(taskRequestDto, existingTask);
        Task updatedTask = taskRepository.save(existingTask);

        log.info("Task updated successfully with ID: {}", updatedTask.getId());
        return mapEntityToDto(updatedTask);
    }

    @Override
    @Transactional
    public void deleteTask(Long id) {
        log.info("Deleting task with ID: {}", id);

        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException("Task not found with ID: " + id);
        }

        taskRepository.deleteById(id);
        log.info("Task deleted successfully with ID: {}", id);
    }

    @Override
    public Page<TaskResponseDto> getTasks(TaskFilterDto filterDto) {
        log.info("Fetching tasks with filters: {}", filterDto);

        Pageable pageable = createPageable(filterDto);
        Page<Task> taskPage;

        if ("high".equals(filterDto.getCurrentFilter()) && filterDto.getPriority() == TaskPriority.HIGH) {
            taskPage = taskRepository.findByPriorityAndStatus(
                    TaskPriority.HIGH, TaskStatus.PENDING, pageable);
        } else if (hasFilters(filterDto)) {
            taskPage = taskRepository.findTasksWithFilters(
                    filterDto.getSearch(),
                    filterDto.getStatus(),
                    filterDto.getPriority(),
                    filterDto.getDueDateFrom(),
                    filterDto.getDueDateTo(),
                    pageable
            );
        } else {
            taskPage = taskRepository.findAll(pageable);
        }

        return taskPage.map(this::mapEntityToDto);
    }

    @Override
    @Transactional
    public TaskResponseDto toggleTaskCompletion(Long id) {
        log.info("Toggling completion status for task with ID: {}", id);

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with ID: " + id));

        if (task.getStatus() == TaskStatus.PENDING) {
            task.markAsCompleted();
        } else {
            task.markAsPending();
        }

        Task updatedTask = taskRepository.save(task);
        log.info("Task completion status toggled for ID: {} to status: {}",
                id, updatedTask.getStatus());

        return mapEntityToDto(updatedTask);
    }

    @Override
    public List<TaskResponseDto> getOverdueTasks() {
        log.info("Fetching overdue tasks");

        List<Task> overdueTasks = taskRepository.findOverdueTasks(
                LocalDate.now(), TaskStatus.PENDING);

        return overdueTasks.stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public TaskStatisticsDto getTaskStatistics() {
        log.info("Calculating task statistics");

        long totalTasks = taskRepository.count();
        long pendingTasks = taskRepository.countByStatus(TaskStatus.PENDING);
        long completedTasks = taskRepository.countByStatus(TaskStatus.COMPLETED);
        long highPriorityTasks = taskRepository.countByPriority(TaskPriority.HIGH);

        List<Task> overdueTasks = taskRepository.findOverdueTasks(
                LocalDate.now(), TaskStatus.PENDING);
        long overdueTasksCount = overdueTasks.size();

        return new TaskStatisticsDto(totalTasks, pendingTasks, completedTasks,
                overdueTasksCount, highPriorityTasks);
    }

    // Helper methods

    private void mapDtoToEntity(TaskRequestDto dto, Task entity) {
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setStatus(dto.getStatus());
        entity.setPriority(dto.getPriority());
        entity.setDueDate(dto.getDueDate());
    }

    private TaskResponseDto mapEntityToDto(Task entity) {
        TaskResponseDto dto = new TaskResponseDto();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setStatus(entity.getStatus());
        dto.setPriority(entity.getPriority());
        dto.setDueDate(entity.getDueDate());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setOverdue(entity.isOverdue());
        dto.setCompleted(entity.isCompleted());
        return dto;
    }

    private Pageable createPageable(TaskFilterDto filterDto) {
        Sort sort = createSort(filterDto.getSortBy(), filterDto.getSortDirection());
        return PageRequest.of(filterDto.getPage(), filterDto.getSize(), sort);
    }

    private Sort createSort(String sortBy, String sortDirection) {
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDirection)
                ? Sort.Direction.ASC : Sort.Direction.DESC;

        // Map frontend field names to entity field names if needed
        String entityFieldName = mapSortField(sortBy);

        return Sort.by(direction, entityFieldName);
    }

    private String mapSortField(String sortBy) {
        return switch (sortBy) {
            case "priority" -> "priority";
            case "dueDate" -> "dueDate";
            case "title" -> "title";
            case "status" -> "status";
            case "createdAt" -> "createdAt";
            case "updatedAt" -> "updatedAt";
            default -> "createdAt";
        };
    }

    private boolean hasFilters(TaskFilterDto filterDto) {
        return filterDto.getSearch() != null ||
                filterDto.getStatus() != null ||
                filterDto.getPriority() != null ||
                filterDto.getDueDateFrom() != null ||
                filterDto.getDueDateTo() != null;
    }
}
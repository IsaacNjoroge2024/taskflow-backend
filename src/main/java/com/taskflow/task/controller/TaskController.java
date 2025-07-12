package com.taskflow.task.controller;

import com.taskflow.task.dto.TaskFilterDto;
import com.taskflow.task.dto.TaskRequestDto;
import com.taskflow.task.dto.TaskResponseDto;
import com.taskflow.task.enums.TaskPriority;
import com.taskflow.task.enums.TaskStatus;
import com.taskflow.task.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
@Slf4j
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponseDto> createTask(@Valid @RequestBody TaskRequestDto taskRequestDto) {
        log.info("REST request to create task: {}", taskRequestDto.getTitle());
        TaskResponseDto createdTask = taskService.createTask(taskRequestDto);
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDto> getTask(@PathVariable Long id) {
        log.info("REST request to get task: {}", id);
        TaskResponseDto task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDto> updateTask(@PathVariable Long id,
                                                      @Valid @RequestBody TaskRequestDto taskRequestDto) {
        log.info("REST request to update task: {}", id);
        TaskResponseDto updatedTask = taskService.updateTask(id, taskRequestDto);
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        log.info("REST request to delete task: {}", id);
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<TaskResponseDto>> getTasks(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDateTo,
            @RequestParam(required = false) Boolean overdue,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {

        log.info("REST request to get tasks with filters - search: {}, status: {}, priority: {}",
                search, status, priority);

        TaskFilterDto filterDto = new TaskFilterDto(search, status, priority, dueDateFrom,
                dueDateTo, overdue, sortBy, sortDirection, page, size);

        Page<TaskResponseDto> tasks = taskService.getTasks(filterDto);

        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(tasks.getTotalElements()))
                .header("X-Page-Number", String.valueOf(tasks.getNumber()))
                .header("X-Page-Size", String.valueOf(tasks.getSize()))
                .body(tasks);
    }

    @PatchMapping("/{id}/toggle-completion")
    public ResponseEntity<TaskResponseDto> toggleTaskCompletion(@PathVariable Long id) {
        log.info("REST request to toggle completion status for task: {}", id);
        TaskResponseDto task = taskService.toggleTaskCompletion(id);
        return ResponseEntity.ok(task);
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<TaskResponseDto>> getOverdueTasks() {
        log.info("REST request to get overdue tasks");
        List<TaskResponseDto> overdueTasks = taskService.getOverdueTasks();
        return ResponseEntity.ok(overdueTasks);
    }

    @GetMapping("/statistics")
    public ResponseEntity<TaskService.TaskStatisticsDto> getTaskStatistics() {
        log.info("REST request to get task statistics");
        TaskService.TaskStatisticsDto statistics = taskService.getTaskStatistics();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("TaskFlow API is running!");
    }
}
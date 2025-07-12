package com.taskflow.task.service;

import com.taskflow.common.exception.TaskNotFoundException;
import com.taskflow.task.dto.TaskFilterDto;
import com.taskflow.task.dto.TaskRequestDto;
import com.taskflow.task.dto.TaskResponseDto;
import com.taskflow.task.entity.Task;
import com.taskflow.task.enums.TaskPriority;
import com.taskflow.task.enums.TaskStatus;
import com.taskflow.task.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    private Task testTask;
    private TaskRequestDto taskRequestDto;

    @BeforeEach
    void setUp() {
        testTask = new Task();
        testTask.setId(1L);
        testTask.setTitle("Test Task");
        testTask.setDescription("Test Description");
        testTask.setStatus(TaskStatus.PENDING);
        testTask.setPriority(TaskPriority.HIGH);
        testTask.setDueDate(LocalDate.now().plusDays(1));
        testTask.setCreatedAt(LocalDateTime.now());
        testTask.setUpdatedAt(LocalDateTime.now());

        taskRequestDto = new TaskRequestDto();
        taskRequestDto.setTitle("Test Task");
        taskRequestDto.setDescription("Test Description");
        taskRequestDto.setStatus(TaskStatus.PENDING);
        taskRequestDto.setPriority(TaskPriority.HIGH);
        taskRequestDto.setDueDate(LocalDate.now().plusDays(1));
    }

    @Test
    void shouldCreateTask() {
        // Given
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // When
        TaskResponseDto result = taskService.createTask(taskRequestDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Test Task");
        assertThat(result.getStatus()).isEqualTo(TaskStatus.PENDING);
        assertThat(result.getPriority()).isEqualTo(TaskPriority.HIGH);

        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void shouldGetTaskById() {
        // Given
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));

        // When
        TaskResponseDto result = taskService.getTaskById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Test Task");

        verify(taskRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenTaskNotFound() {
        // Given
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> taskService.getTaskById(1L))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessage("Task not found with ID: 1");
    }

    @Test
    void shouldUpdateTask() {
        // Given
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        TaskRequestDto updateDto = new TaskRequestDto();
        updateDto.setTitle("Updated Task");
        updateDto.setDescription("Updated Description");
        updateDto.setStatus(TaskStatus.COMPLETED);
        updateDto.setPriority(TaskPriority.LOW);

        // When
        TaskResponseDto result = taskService.updateTask(1L, updateDto);

        // Then
        assertThat(result).isNotNull();
        verify(taskRepository).findById(1L);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void shouldDeleteTask() {
        // Given
        when(taskRepository.existsById(1L)).thenReturn(true);

        // When
        taskService.deleteTask(1L);

        // Then
        verify(taskRepository).existsById(1L);
        verify(taskRepository).deleteById(1L);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentTask() {
        // Given
        when(taskRepository.existsById(1L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> taskService.deleteTask(1L))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessage("Task not found with ID: 1");
    }

    @Test
    void shouldGetTasksWithFilters() {
        // Given
        List<Task> tasks = Arrays.asList(testTask);
        Page<Task> taskPage = new PageImpl<>(tasks, PageRequest.of(0, 20), 1);

        when(taskRepository.findTasksWithFilters(
                any(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(taskPage);

        TaskFilterDto filterDto = new TaskFilterDto();
        filterDto.setSearch("test");
        filterDto.setStatus(TaskStatus.PENDING);

        // When
        Page<TaskResponseDto> result = taskService.getTasks(filterDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Test Task");

        verify(taskRepository).findTasksWithFilters(
                eq("test"), eq(TaskStatus.PENDING), any(), any(), any(), any(Pageable.class));
    }

    @Test
    void shouldToggleTaskCompletion() {
        // Given
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // When
        TaskResponseDto result = taskService.toggleTaskCompletion(1L);

        // Then
        assertThat(result).isNotNull();
        verify(taskRepository).findById(1L);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void shouldGetOverdueTasks() {
        // Given
        Task overdueTask = new Task();
        overdueTask.setId(2L);
        overdueTask.setTitle("Overdue Task");
        overdueTask.setStatus(TaskStatus.PENDING);
        overdueTask.setDueDate(LocalDate.now().minusDays(1));
        overdueTask.setCreatedAt(LocalDateTime.now());
        overdueTask.setUpdatedAt(LocalDateTime.now());

        when(taskRepository.findOverdueTasks(any(LocalDate.class), eq(TaskStatus.PENDING)))
                .thenReturn(Arrays.asList(overdueTask));

        // When
        List<TaskResponseDto> result = taskService.getOverdueTasks();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Overdue Task");
        assertThat(result.get(0).isOverdue()).isTrue();
    }

    @Test
    void shouldGetTaskStatistics() {
        // Given
        when(taskRepository.count()).thenReturn(10L);
        when(taskRepository.countByStatus(TaskStatus.PENDING)).thenReturn(6L);
        when(taskRepository.countByStatus(TaskStatus.COMPLETED)).thenReturn(4L);
        when(taskRepository.countByPriority(TaskPriority.HIGH)).thenReturn(3L);
        when(taskRepository.findOverdueTasks(any(LocalDate.class), eq(TaskStatus.PENDING)))
                .thenReturn(Arrays.asList(testTask));

        // When
        TaskService.TaskStatisticsDto result = taskService.getTaskStatistics();

        // Then
        assertThat(result.getTotalTasks()).isEqualTo(10L);
        assertThat(result.getPendingTasks()).isEqualTo(6L);
        assertThat(result.getCompletedTasks()).isEqualTo(4L);
        assertThat(result.getHighPriorityTasks()).isEqualTo(3L);
        assertThat(result.getOverdueTasks()).isEqualTo(1L);
    }
}
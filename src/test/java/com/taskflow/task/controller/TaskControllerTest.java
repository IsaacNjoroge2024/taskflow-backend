package com.taskflow.task.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskflow.task.dto.TaskRequestDto;
import com.taskflow.task.dto.TaskResponseDto;
import com.taskflow.task.enums.TaskPriority;
import com.taskflow.task.enums.TaskStatus;
import com.taskflow.task.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = TaskController.class, excludeAutoConfiguration = {
        JpaRepositoriesAutoConfiguration.class
})
@ActiveProfiles("test")
class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    private TaskResponseDto taskResponseDto;
    private TaskRequestDto taskRequestDto;

    @BeforeEach
    void setUp() {
        taskResponseDto = new TaskResponseDto();
        taskResponseDto.setId(1L);
        taskResponseDto.setTitle("Test Task");
        taskResponseDto.setDescription("Test Description");
        taskResponseDto.setStatus(TaskStatus.PENDING);
        taskResponseDto.setPriority(TaskPriority.HIGH);
        taskResponseDto.setDueDate(LocalDate.now().plusDays(1));
        taskResponseDto.setCreatedAt(LocalDateTime.now());
        taskResponseDto.setUpdatedAt(LocalDateTime.now());
        taskResponseDto.setOverdue(false);
        taskResponseDto.setCompleted(false);

        taskRequestDto = new TaskRequestDto();
        taskRequestDto.setTitle("Test Task");
        taskRequestDto.setDescription("Test Description");
        taskRequestDto.setStatus(TaskStatus.PENDING);
        taskRequestDto.setPriority(TaskPriority.HIGH);
        taskRequestDto.setDueDate(LocalDate.now().plusDays(1));
    }

    @Test
    void shouldCreateTask() throws Exception {
        // Given
        when(taskService.createTask(any(TaskRequestDto.class))).thenReturn(taskResponseDto);

        // When & Then
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Test Task")))
                .andExpect(jsonPath("$.status", is("PENDING")))
                .andExpect(jsonPath("$.priority", is("HIGH")));

        verify(taskService).createTask(any(TaskRequestDto.class));
    }

    @Test
    void shouldGetTaskById() throws Exception {
        // Given
        when(taskService.getTaskById(1L)).thenReturn(taskResponseDto);

        // When & Then
        mockMvc.perform(get("/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Test Task")));

        verify(taskService).getTaskById(1L);
    }

    @Test
    void shouldUpdateTask() throws Exception {
        // Given
        when(taskService.updateTask(eq(1L), any(TaskRequestDto.class))).thenReturn(taskResponseDto);

        // When & Then
        mockMvc.perform(put("/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Test Task")));

        verify(taskService).updateTask(eq(1L), any(TaskRequestDto.class));
    }

    @Test
    void shouldDeleteTask() throws Exception {
        // When & Then
        mockMvc.perform(delete("/tasks/1"))
                .andExpect(status().isNoContent());

        verify(taskService).deleteTask(1L);
    }

    @Test
    void shouldGetAllTasks() throws Exception {
        // Given
        List<TaskResponseDto> tasks = Arrays.asList(taskResponseDto);
        Page<TaskResponseDto> taskPage = new PageImpl<>(tasks, PageRequest.of(0, 20), 1);

        when(taskService.getTasks(any())).thenReturn(taskPage);

        // When & Then
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].title", is("Test Task")))
                .andExpect(header().string("X-Total-Count", "1"));

        verify(taskService).getTasks(any());
    }

    @Test
    void shouldGetTasksWithFilters() throws Exception {
        // Given
        List<TaskResponseDto> tasks = Arrays.asList(taskResponseDto);
        Page<TaskResponseDto> taskPage = new PageImpl<>(tasks, PageRequest.of(0, 20), 1);

        when(taskService.getTasks(any())).thenReturn(taskPage);

        // When & Then
        mockMvc.perform(get("/tasks")
                        .param("search", "test")
                        .param("status", "PENDING")
                        .param("priority", "HIGH")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));

        verify(taskService).getTasks(any());
    }

    @Test
    void shouldToggleTaskCompletion() throws Exception {
        // Given
        taskResponseDto.setStatus(TaskStatus.COMPLETED);
        taskResponseDto.setCompleted(true);
        when(taskService.toggleTaskCompletion(1L)).thenReturn(taskResponseDto);

        // When & Then
        mockMvc.perform(patch("/tasks/1/toggle-completion"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("COMPLETED")))
                .andExpect(jsonPath("$.completed", is(true)));

        verify(taskService).toggleTaskCompletion(1L);
    }

    @Test
    void shouldGetOverdueTasks() throws Exception {
        // Given
        List<TaskResponseDto> overdueTasks = Arrays.asList(taskResponseDto);
        when(taskService.getOverdueTasks()).thenReturn(overdueTasks);

        // When & Then
        mockMvc.perform(get("/tasks/overdue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Test Task")));

        verify(taskService).getOverdueTasks();
    }

    @Test
    void shouldGetTaskStatistics() throws Exception {
        // Given
        TaskService.TaskStatisticsDto statistics =
                new TaskService.TaskStatisticsDto(10, 6, 4, 1, 3);
        when(taskService.getTaskStatistics()).thenReturn(statistics);

        // When & Then
        mockMvc.perform(get("/tasks/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalTasks", is(10)))
                .andExpect(jsonPath("$.pendingTasks", is(6)))
                .andExpect(jsonPath("$.completedTasks", is(4)))
                .andExpect(jsonPath("$.overdueTasks", is(1)))
                .andExpect(jsonPath("$.highPriorityTasks", is(3)));

        verify(taskService).getTaskStatistics();
    }

    @Test
    void shouldReturnBadRequestForInvalidTask() throws Exception {
        // Given
        TaskRequestDto invalidTask = new TaskRequestDto();
        invalidTask.setTitle(""); // Invalid: empty title
        invalidTask.setStatus(TaskStatus.PENDING);
        invalidTask.setPriority(TaskPriority.HIGH);

        // When & Then
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidTask)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnHealthCheck() throws Exception {
        // When & Then
        mockMvc.perform(get("/tasks/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("TaskFlow API is running!"));
    }
}
package com.taskflow.task.repository;

import com.taskflow.task.entity.Task;
import com.taskflow.task.enums.TaskPriority;
import com.taskflow.task.enums.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class TaskRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TaskRepository taskRepository;

    private Task testTask;

    @BeforeEach
    void setUp() {
        testTask = new Task();
        testTask.setTitle("Test Task");
        testTask.setDescription("Test Description");
        testTask.setStatus(TaskStatus.PENDING);
        testTask.setPriority(TaskPriority.HIGH);
        testTask.setDueDate(LocalDate.now().plusDays(1));
    }

    @Test
    void shouldSaveAndFindTask() {
        // Given
        Task savedTask = entityManager.persistAndFlush(testTask);

        // When
        Task foundTask = taskRepository.findById(savedTask.getId()).orElse(null);

        // Then
        assertThat(foundTask).isNotNull();
        assertThat(foundTask.getTitle()).isEqualTo("Test Task");
        assertThat(foundTask.getStatus()).isEqualTo(TaskStatus.PENDING);
        assertThat(foundTask.getPriority()).isEqualTo(TaskPriority.HIGH);
    }

    @Test
    void shouldFindTasksByStatus() {
        // Given
        entityManager.persistAndFlush(testTask);

        Task completedTask = new Task();
        completedTask.setTitle("Completed Task");
        completedTask.setDescription("Completed Description");
        completedTask.setStatus(TaskStatus.COMPLETED);
        completedTask.setPriority(TaskPriority.LOW);
        entityManager.persistAndFlush(completedTask);

        // When
        Page<Task> pendingTasks = taskRepository.findByStatus(TaskStatus.PENDING, PageRequest.of(0, 10));
        Page<Task> completedTasks = taskRepository.findByStatus(TaskStatus.COMPLETED, PageRequest.of(0, 10));

        // Then
        assertThat(pendingTasks.getContent()).hasSize(1);
        assertThat(pendingTasks.getContent().get(0).getTitle()).isEqualTo("Test Task");

        assertThat(completedTasks.getContent()).hasSize(1);
        assertThat(completedTasks.getContent().get(0).getTitle()).isEqualTo("Completed Task");
    }

    @Test
    void shouldFindOverdueTasks() {
        // Given
        Task overdueTask = new Task();
        overdueTask.setTitle("Overdue Task");
        overdueTask.setDescription("Overdue Description");
        overdueTask.setStatus(TaskStatus.PENDING);
        overdueTask.setPriority(TaskPriority.HIGH);
        overdueTask.setDueDate(LocalDate.now().minusDays(1)); // Yesterday
        entityManager.persistAndFlush(overdueTask);

        entityManager.persistAndFlush(testTask); // Future due date

        // When
        List<Task> overdueTasks = taskRepository.findOverdueTasks(LocalDate.now(), TaskStatus.PENDING);

        // Then
        assertThat(overdueTasks).hasSize(1);
        assertThat(overdueTasks.get(0).getTitle()).isEqualTo("Overdue Task");
    }

    @Test
    void shouldSearchTasksByTitleAndDescription() {
        // Given
        entityManager.persistAndFlush(testTask);

        Task anotherTask = new Task();
        anotherTask.setTitle("Different Title");
        anotherTask.setDescription("Test content in description");
        anotherTask.setStatus(TaskStatus.PENDING);
        anotherTask.setPriority(TaskPriority.MEDIUM);
        entityManager.persistAndFlush(anotherTask);

        // When
        Page<Task> searchResults = taskRepository
                .findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                        "test", PageRequest.of(0, 10));

        // Then
        assertThat(searchResults.getContent()).hasSize(2);
    }

    @Test
    void shouldCountTasksByStatus() {
        // Given
        entityManager.persistAndFlush(testTask);

        Task completedTask = new Task();
        completedTask.setTitle("Completed Task");
        completedTask.setDescription("Completed Description");
        completedTask.setStatus(TaskStatus.COMPLETED);
        completedTask.setPriority(TaskPriority.LOW);
        entityManager.persistAndFlush(completedTask);

        // When
        long pendingCount = taskRepository.countByStatus(TaskStatus.PENDING);
        long completedCount = taskRepository.countByStatus(TaskStatus.COMPLETED);

        // Then
        assertThat(pendingCount).isEqualTo(1);
        assertThat(completedCount).isEqualTo(1);
    }
}

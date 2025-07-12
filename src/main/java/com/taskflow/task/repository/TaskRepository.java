package com.taskflow.task.repository;

import com.taskflow.task.entity.Task;
import com.taskflow.task.enums.TaskPriority;
import com.taskflow.task.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findByStatus(TaskStatus status, Pageable pageable);

    Page<Task> findByPriority(TaskPriority priority, Pageable pageable);

    Page<Task> findByStatusAndPriority(TaskStatus status, TaskPriority priority, Pageable pageable);

    @Query("SELECT t FROM Task t WHERE " +
            "LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Task> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            @Param("search") String search, Pageable pageable);

    @Query("SELECT t FROM Task t WHERE t.dueDate < :currentDate AND t.status = :status")
    List<Task> findOverdueTasks(@Param("currentDate") LocalDate currentDate,
                                @Param("status") TaskStatus status);

    @Query("SELECT t FROM Task t WHERE t.dueDate BETWEEN :startDate AND :endDate")
    Page<Task> findTasksDueBetween(@Param("startDate") LocalDate startDate,
                                   @Param("endDate") LocalDate endDate,
                                   Pageable pageable);

    long countByStatus(TaskStatus status);

    long countByPriority(TaskPriority priority);

    @Query("SELECT t FROM Task t WHERE " +
            "(:search IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            " LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
            "(:status IS NULL OR t.status = :status) AND " +
            "(:priority IS NULL OR t.priority = :priority) AND " +
            "(:dueDateFrom IS NULL OR t.dueDate >= :dueDateFrom) AND " +
            "(:dueDateTo IS NULL OR t.dueDate <= :dueDateTo)")
    Page<Task> findTasksWithFilters(@Param("search") String search,
                                    @Param("status") TaskStatus status,
                                    @Param("priority") TaskPriority priority,
                                    @Param("dueDateFrom") LocalDate dueDateFrom,
                                    @Param("dueDateTo") LocalDate dueDateTo,
                                    Pageable pageable);
}
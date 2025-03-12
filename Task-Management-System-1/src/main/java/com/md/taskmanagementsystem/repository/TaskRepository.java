package com.md.taskmanagementsystem.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.md.taskmanagementsystem.enums.TaskPriority;
import com.md.taskmanagementsystem.enums.TaskStatus;
import com.md.taskmanagementsystem.model.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {

    // Find all tasks for a specific user
    List<Task> findByUserId(Long userId);

    // Find tasks by user and status (PENDING, IN_PROGRESS, COMPLETED)
    List<Task> findByUserIdAndStatus(Long userId, TaskStatus status);

    // Find tasks by user and status (HIGH, LOW, MEDIUM,URGENT)
    List<Task> findByUserIdAndPriority(Long userId, TaskPriority priority);

    // Count the number of completed tasks for a user
    long countByUserIdAndStatus(Long userId, TaskStatus status);
    
    // Fetch latest 5 tasks using Pageable
    @Query("SELECT t FROM Task t WHERE t.user.id = :userId ORDER BY t.createdAt DESC")
    List<Task> findLatestTasksByUserId(@Param("userId") Long userId, Pageable pageable);
    
    // Find tasks by status and due date
    @Query("SELECT t FROM Task t WHERE t.status IN :statuses AND DATE(t.dueDate) = :dueDate")
    List<Task> findByStatusInAndDueDate(@Param("statuses") List<TaskStatus> statuses, @Param("dueDate") LocalDate dueDate);

    // Find tasks by status and due date between a time range
    @Query("SELECT t FROM Task t WHERE t.status IN :statuses AND t.dueDate BETWEEN :startDateTime AND :endDateTime")
    List<Task> findByStatusInAndDueDateBetween(
        @Param("statuses") List<TaskStatus> statuses,
        @Param("startDateTime") LocalDateTime startDateTime,
        @Param("endDateTime") LocalDateTime endDateTime
    );
    
    // Find task by ID and user ID to ensure ownership
    @Query("SELECT t FROM Task t WHERE t.id = :taskId AND t.user.id = :userId")
    Task findByIdAndUserId(@Param("taskId") Long taskId, @Param("userId") Long userId);
}

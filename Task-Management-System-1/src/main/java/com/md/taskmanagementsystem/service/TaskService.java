package com.md.taskmanagementsystem.service;

import java.util.List;
import java.util.Optional;

import com.md.taskmanagementsystem.enums.TaskPriority;
import com.md.taskmanagementsystem.enums.TaskStatus;
import com.md.taskmanagementsystem.model.Task;

public interface TaskService {
    /**
     * Creates a new task for a user
     * @param task The task to create
     * @return The created task
     */
    Task createTask(Task task);

    /**
     * Retrieves a task by its ID
     * @param taskId The ID of the task
     * @return Optional containing the task if found
     */
    Optional<Task> getTaskById(Long taskId);

    /**
     * Retrieves all tasks for a specific user
     * @param userId The ID of the user
     * @return List of tasks belonging to the user
     */
    List<Task> getTasksByUserId(Long userId);

    /**
     * Retrieves tasks for a user filtered by status
     * @param userId The ID of the user
     * @param status The status to filter by
     * @return List of filtered tasks
     */
    List<Task> getTasksByUserIdAndStatus(Long userId, TaskStatus status);

    /**
     * Retrieves tasks for a user filtered by priority
     * @param userId The ID of the user
     * @param priority The priority to filter by
     * @return List of filtered tasks
     */
    List<Task> getTasksByUserIdAndPriority(Long userId, TaskPriority priority);

    /**
     * Updates an existing task
     * @param taskId The ID of the task to update
     * @param updatedTask The updated task data
     * @return The updated task
     * @throws AccessDeniedException if the current user doesn't own the task
     */
    Task updateTask(Long taskId, Task updatedTask);

    /**
     * Deletes a task
     * @param taskId The ID of the task to delete
     * @throws AccessDeniedException if the current user doesn't own the task
     */
    void deleteTask(Long taskId);

    /**
     * Counts completed tasks for a user
     * @param userId The ID of the user
     * @return Count of completed tasks
     */
    long countCompletedTasksByUserId(Long userId);

    /**
     * Counts in-progress tasks for a user
     * @param userId The ID of the user
     * @return Count of in-progress tasks
     */
    long countInProgressTasksByUserId(Long userId);

    /**
     * Gets the latest tasks for a user
     * @param userId The ID of the user
     * @return List of latest tasks
     */
    List<Task> getLatestTasksByUserId(Long userId);

    /**
     * Sends reminders for tasks
     */
    void sendTaskReminders();
}

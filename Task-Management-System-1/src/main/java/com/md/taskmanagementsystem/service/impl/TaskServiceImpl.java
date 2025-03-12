package com.md.taskmanagementsystem.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.md.taskmanagementsystem.enums.TaskPriority;
import com.md.taskmanagementsystem.enums.TaskStatus;
import com.md.taskmanagementsystem.exception.TaskNotFoundException;
import com.md.taskmanagementsystem.model.Task;
import com.md.taskmanagementsystem.model.User;
import com.md.taskmanagementsystem.repository.TaskRepository;
import com.md.taskmanagementsystem.repository.UserRepository;
import com.md.taskmanagementsystem.service.EmailService;
import com.md.taskmanagementsystem.service.TaskService;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm");

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EmailService emailService;

    @Override
    public Task createTask(Task task) {
        if (task.getUser() == null || task.getUser().getId() == null) {
            throw new IllegalArgumentException("User ID is required to create a task.");
        }
        Long userId = task.getUser().getId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new TaskNotFoundException("User with ID " + userId + " not found!"));

        task.setUser(user);
        return taskRepository.save(task);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Task> getTaskById(Long taskId) {
        return taskRepository.findById(taskId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> getTasksByUserId(Long userId) {
        List<Task> tasks = taskRepository.findByUserId(userId);
        return tasks != null ? tasks : new ArrayList<>();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> getTasksByUserIdAndStatus(Long userId, TaskStatus status) {
        List<Task> tasks = taskRepository.findByUserIdAndStatus(userId, status);
        return tasks != null ? tasks : new ArrayList<>();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> getTasksByUserIdAndPriority(Long userId, TaskPriority priority) {
        List<Task> tasks = taskRepository.findByUserIdAndPriority(userId, priority);
        return tasks != null ? tasks : new ArrayList<>();
    }

    @Override
    public Task updateTask(Long taskId, Task updatedTask) {
        return taskRepository.findById(taskId)
                .map(existingTask -> {
                    String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
                    if (!existingTask.getUser().getUsername().equals(currentUsername)) {
                        throw new AccessDeniedException("You don't have permission to update this task!");
                    }

                    existingTask.setTitle(updatedTask.getTitle());
                    existingTask.setDescription(updatedTask.getDescription());
                    existingTask.setDueDate(updatedTask.getDueDate());
                    existingTask.setPriority(updatedTask.getPriority());
                    existingTask.setStatus(updatedTask.getStatus());

                    return taskRepository.save(existingTask);
                })
                .orElseThrow(() -> new TaskNotFoundException("Task with ID " + taskId + " not found!"));
    }

    @Override
    public void deleteTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task with ID " + taskId + " not found!"));

        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!task.getUser().getUsername().equals(currentUsername)) {
            throw new AccessDeniedException("You don't have permission to delete this task!");
        }

        taskRepository.deleteById(taskId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countCompletedTasksByUserId(Long userId) {
        return taskRepository.countByUserIdAndStatus(userId, TaskStatus.COMPLETED);
    }

    @Override
    @Transactional(readOnly = true)
    public long countInProgressTasksByUserId(Long userId) {
        return taskRepository.countByUserIdAndStatus(userId, TaskStatus.IN_PROGRESS);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> getLatestTasksByUserId(Long userId) {
        Pageable pageable = PageRequest.of(0, 5);
        List<Task> tasks = taskRepository.findLatestTasksByUserId(userId, pageable);
        return tasks != null ? tasks : new ArrayList<>();
    }
    
    @Override
    @Scheduled(cron = "0 0 9 * * ?") // Runs every day at 9 AM
    @Transactional
    public void sendTaskReminders() {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        
        LocalDateTime tomorrowStart = tomorrow.atStartOfDay();
        LocalDateTime tomorrowEnd = tomorrow.atTime(LocalTime.MAX);

        List<Task> tasks = taskRepository.findByStatusInAndDueDateBetween(
            List.of(TaskStatus.PENDING, TaskStatus.IN_PROGRESS),
            tomorrowStart,
            tomorrowEnd
        );

        for (Task task : tasks) {
            // Fetch the user eagerly to avoid lazy loading issues
            User user = userRepository.findById(task.getUser().getId())
                    .orElseThrow(() -> new TaskNotFoundException("User not found for task: " + task.getId()));
            
            if (user.isEmailNotificationsEnabled()) {
                sendDueDateReminderEmail(task, user);
            }
        }
    }

    private void sendDueDateReminderEmail(Task task, User user) {
        String email = user.getEmail();
        String subject = "Task Due Tomorrow: " + task.getTitle();
        String name = user.getFirstName() + " " + user.getLastName();
        
        Map<String, Object> model = Map.of(
            "userName", name,
            "taskTitle", task.getTitle(),
            "dueDate", task.getDueDate().format(DATE_FORMATTER)
        );

        emailService.sendEmail(email, subject, "email/task_due_reminder", model);
    }
}

package com.md.taskmanagementsystem.controller;

import com.md.taskmanagementsystem.enums.TaskPriority;
import com.md.taskmanagementsystem.enums.TaskStatus;
import com.md.taskmanagementsystem.model.Task;
import com.md.taskmanagementsystem.model.User;
import com.md.taskmanagementsystem.service.TaskService;
import com.md.taskmanagementsystem.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<User> user = userService.getUserByUsername(username);
        return user.orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping
    public String listTasks(Model model) {
        User user = getAuthenticatedUser();
        List<Task> tasks = taskService.getTasksByUserId(user.getId());

        model.addAttribute("pageTitle", "Task List - Task Management System");
        model.addAttribute("tasks", tasks);
        if (tasks.isEmpty()) {
            model.addAttribute("noTasksMessage", "You have no tasks. Start by adding a new one!");
        }
        return "task/list";
    }

    @GetMapping("/create")
    public String showCreateTaskForm(Model model) {
        model.addAttribute("pageTitle", "Create Task - Task Management System");
        model.addAttribute("task", new Task());
        model.addAttribute("priorities", TaskPriority.values());
        model.addAttribute("statuses", TaskStatus.values());
        return "task/create";
    }
   
    @PostMapping("/create")
    public String createTask(@ModelAttribute Task task, RedirectAttributes redirectAttributes) {
        User user = getAuthenticatedUser();
        task.setUser(user);
        taskService.createTask(task);
        redirectAttributes.addFlashAttribute("message", "Task created successfully!");
        return "redirect:/tasks";
    }

    @GetMapping("/edit/{id}")
    public String showEditTaskForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        User currentUser = getAuthenticatedUser();
        Optional<Task> taskOpt = taskService.getTaskById(id);
        
        if (taskOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Task not found!");
            return "redirect:/tasks";
        }
        
        Task task = taskOpt.get();
        // Check if the task belongs to the current user
        if (!task.getUser().getId().equals(currentUser.getId())) {
            redirectAttributes.addFlashAttribute("error", "You don't have permission to edit this task!");
            return "redirect:/tasks";
        }

        model.addAttribute("pageTitle", "Edit Task - Task Management System");
        model.addAttribute("task", task);
        model.addAttribute("priorities", TaskPriority.values());
        model.addAttribute("statuses", TaskStatus.values());
        return "task/edit";
    }

    @PostMapping("/update/{id}")
    public String updateTask(@PathVariable Long id, @ModelAttribute Task updatedTask, RedirectAttributes redirectAttributes) {
        User currentUser = getAuthenticatedUser();
        Optional<Task> taskOpt = taskService.getTaskById(id);
        
        if (taskOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Task not found!");
            return "redirect:/tasks";
        }
        
        Task existingTask = taskOpt.get();
        // Check if the task belongs to the current user
        if (!existingTask.getUser().getId().equals(currentUser.getId())) {
            redirectAttributes.addFlashAttribute("error", "You don't have permission to update this task!");
            return "redirect:/tasks";
        }

        taskService.updateTask(id, updatedTask);
        redirectAttributes.addFlashAttribute("message", "Task updated successfully!");
        return "redirect:/tasks";
    }

    @PostMapping("/delete/{id}")
    public String deleteTask(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        User currentUser = getAuthenticatedUser();
        Optional<Task> taskOpt = taskService.getTaskById(id);
        
        if (taskOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Task not found!");
            return "redirect:/tasks";
        }
        
        Task task = taskOpt.get();
        // Check if the task belongs to the current user
        if (!task.getUser().getId().equals(currentUser.getId())) {
            redirectAttributes.addFlashAttribute("error", "You don't have permission to delete this task!");
            return "redirect:/tasks";
        }

        taskService.deleteTask(id);
        redirectAttributes.addFlashAttribute("message", "Task deleted successfully!");
        return "redirect:/tasks";
    }
}

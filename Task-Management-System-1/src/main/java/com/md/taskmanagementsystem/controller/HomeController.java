package com.md.taskmanagementsystem.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.md.taskmanagementsystem.enums.TaskStatus;
import com.md.taskmanagementsystem.model.Task;
import com.md.taskmanagementsystem.model.User;
import com.md.taskmanagementsystem.service.TaskService;
import com.md.taskmanagementsystem.service.UserService;

@Controller
public class HomeController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userService.getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping("/")
    public String showIndexPage() {
        // If user is authenticated, redirect to home
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return "redirect:/home";
        }
        return "index";
    }

    @GetMapping("/home")
    public String home(Model model) {
        try {
            User user = getAuthenticatedUser();
            Long userId = user.getId();

            // Get all tasks
            List<Task> allTasks = taskService.getTasksByUserId(userId);

            // Group tasks by status
            Map<TaskStatus, List<Task>> tasksByStatus = allTasks.stream()
                    .collect(Collectors.groupingBy(Task::getStatus));

            // Calculate statistics
            long totalTasks = allTasks.size();
            long completedTasks = tasksByStatus.getOrDefault(TaskStatus.COMPLETED, Collections.emptyList()).size();
            long pendingTasks = tasksByStatus.getOrDefault(TaskStatus.PENDING, Collections.emptyList()).size();
            long inProgressTasks = tasksByStatus.getOrDefault(TaskStatus.IN_PROGRESS, Collections.emptyList()).size();

            // Add attributes to model
            model.addAttribute("user", user);
            model.addAttribute("tasks", allTasks);
            model.addAttribute("tasksByStatus", tasksByStatus);
            model.addAttribute("totalTasks", totalTasks);
            model.addAttribute("completedTasks", completedTasks);
            model.addAttribute("pendingTasks", pendingTasks);
            model.addAttribute("inProgressTasks", inProgressTasks);

            return "home";
        } catch (Exception e) {
            // Log the error
            e.printStackTrace();
            
            // Add error message to model
            model.addAttribute("error", "An error occurred while loading your dashboard. Please try again.");
            return "home";
        }
    }
}

package com.md.taskmanagementsystem.controller;

import com.md.taskmanagementsystem.model.User;
import com.md.taskmanagementsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserService userService;

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<User> user = userService.getUserByUsername(username);
        return user.orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping("/show")
    public String showProfile(Model model, @RequestParam(value = "message", required = false) String message) {
        User user = getAuthenticatedUser();
        model.addAttribute("user", user);
        model.addAttribute("message", message);
        return "profile/show";
    }

    @GetMapping("/edit")
    public String editProfile(Model model) {
        User user = getAuthenticatedUser();
        model.addAttribute("user", user);
        return "profile/edit";
    }

    @PostMapping("/update")
    public String updateProfile(@ModelAttribute User updatedUser, RedirectAttributes redirectAttributes) {
        User user = getAuthenticatedUser();
        userService.updateUser(user.getId(), updatedUser);
        redirectAttributes.addFlashAttribute("message", "Profile updated successfully!");
        return "redirect:/profile/show";
    }

    @PostMapping("/delete")
    public String deleteAccount() {
        User user = getAuthenticatedUser();
        userService.deleteUser(user.getId());
        SecurityContextHolder.clearContext(); // Logout user after account deletion
        return "redirect:/logout";
    }

    //Provide Email notifications
    @PostMapping("/toggle-email-notifications")
    public String toggleEmailNotifications(RedirectAttributes redirectAttributes) {
        User user = getAuthenticatedUser();
        user.setEmailNotificationsEnabled(!user.isEmailNotificationsEnabled()); // Toggle the status

        userService.updateUser(user.getId(), user); // Use your existing updateUser() method

        String statusMessage = user.isEmailNotificationsEnabled() 
                               ? "Email notifications enabled!" 
                               : "Email notifications disabled!";
        redirectAttributes.addFlashAttribute("message", statusMessage);
        return "redirect:/profile/show";
    }

}

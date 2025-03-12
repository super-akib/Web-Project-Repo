package com.md.taskmanagementsystem.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Handle User Not Found Exception
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleUserNotFoundException(UserNotFoundException ex, Model model, HttpServletRequest request) {
        populateErrorAttributes(model, HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
        return "error/custom-error";
    }

    // Handle Task Not Found Exception
    @ExceptionHandler(TaskNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleTaskNotFoundException(TaskNotFoundException ex, Model model, HttpServletRequest request) {
        populateErrorAttributes(model, HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
        return "error/custom-error";
    }

    // Handle Generic Exception (Fallback)
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGlobalException(Exception ex, Model model, HttpServletRequest request) {
        populateErrorAttributes(model, HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", request.getRequestURI());
        return "error/custom-error";
    }

    // Utility method to populate error attributes
    private void populateErrorAttributes(Model model, HttpStatus status, String message, String path) {
        model.addAttribute("timestamp", LocalDateTime.now());
        model.addAttribute("status", status.value());
        model.addAttribute("error", status.getReasonPhrase());
        model.addAttribute("message", message);
        model.addAttribute("path", path);
    }
}

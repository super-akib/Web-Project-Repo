package com.md.taskmanagementsystem.exception;

@SuppressWarnings("serial")
public class TaskOperationException extends RuntimeException {
    public TaskOperationException(String message) {
        super(message);
    }
}

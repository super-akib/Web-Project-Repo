package com.md.taskmanagementsystem.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.md.taskmanagementsystem.enums.TaskPriority;
import com.md.taskmanagementsystem.enums.TaskStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tasks_table")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Task {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "Title is required")
	private String title;

	@Column(columnDefinition = "TEXT")
	private String description;

	@NotNull(message = "Due date is required")
	private LocalDateTime dueDate;

	@Enumerated(EnumType.STRING)
	private TaskStatus status = TaskStatus.PENDING;

	@Enumerated(EnumType.STRING)
	private TaskPriority priority = TaskPriority.MEDIUM;

	@CreationTimestamp
	@Column(updatable = false) // prevents updates
	private LocalDateTime createdAt;
	@UpdateTimestamp
	private LocalDateTime updatedAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;
}

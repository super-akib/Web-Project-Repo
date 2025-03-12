# Task Management System - Technical Documentation

## Project Overview
The Task Management System is a web-based application built using Spring Boot that allows users to manage their tasks efficiently. The system provides features for task creation, updating, deletion, and tracking task status with email notifications for upcoming deadlines.

## Technical Stack
- **Backend**: Spring Boot 3.x
- **Frontend**: Thymeleaf, Bootstrap, jQuery
- **Database**: MySQL
- **Security**: Spring Security
- **Build Tool**: Maven
- **Java Version**: 17

## Core Components

### 1. Models

#### User.java
- **Purpose**: Represents user data and implements Spring Security's UserDetails
- **Key Fields**:
  - id: Unique identifier
  - firstName, lastName: User's name
  - email: User's email (unique)
  - username: Login username (unique)
  - password: Encrypted password
  - listOfTask: One-to-many relationship with tasks
  - emailNotificationsEnabled: Toggle for email notifications
- **Key Methods**:
  - getAuthorities(): Returns user roles (ROLE_USER)
  - UserDetails interface implementations for security

#### Task.java
- **Purpose**: Represents a task entity
- **Key Fields**:
  - id: Unique identifier
  - title: Task title
  - description: Task details
  - dueDate: Task deadline
  - status: PENDING, IN_PROGRESS, COMPLETED
  - priority: LOW, MEDIUM, HIGH, URGENT
  - user: Many-to-one relationship with User

### 2. Controllers

#### HomeController.java
- **Purpose**: Handles main application routing and dashboard
- **Key Endpoints**:
  - GET /: Shows landing page
  - GET /home: Displays user dashboard with task statistics
- **Key Methods**:
  - home(): Aggregates task statistics and user data
  - getAuthenticatedUser(): Retrieves current user

#### TaskController.java
- **Purpose**: Manages task-related operations
- **Key Endpoints**:
  - GET /tasks/create: Shows task creation form
  - POST /tasks/save: Creates new task
  - GET /tasks/edit/{id}: Shows task edit form
  - POST /tasks/update/{id}: Updates existing task
  - GET /tasks/delete/{id}: Deletes task
- **Key Methods**:
  - createTask(): Handles task creation
  - updateTask(): Processes task updates
  - deleteTask(): Removes tasks

#### AuthController.java
- **Purpose**: Handles authentication and registration
- **Key Endpoints**:
  - GET /register: Shows registration form
  - POST /register: Creates new user
  - GET /login: Shows login form
- **Key Methods**:
  - registerUser(): Processes user registration
  - showLoginForm(): Displays login page

### 3. Services

#### TaskServiceImpl.java
- **Purpose**: Implements task business logic
- **Key Methods**:
  - createTask(): Creates new tasks
  - updateTask(): Updates existing tasks
  - deleteTask(): Removes tasks
  - getTasksByUserId(): Retrieves user's tasks
  - countCompletedTasksByUserId(): Counts completed tasks
  - sendTaskReminders(): Scheduled job for email notifications
  - sendDueDateReminderEmail(): Sends reminder emails

#### UserServiceImpl.java
- **Purpose**: Implements user management logic
- **Key Methods**:
  - createUser(): Registers new users
  - getUserByUsername(): Retrieves user by username
  - updateUser(): Updates user information

#### CustomUserDetailsService.java
- **Purpose**: Implements Spring Security UserDetailsService
- **Key Methods**:
  - loadUserByUsername(): Loads user for authentication

#### EmailService.java
- **Purpose**: Handles email notifications
- **Key Methods**:
  - sendEmail(): Sends formatted emails using templates
  - processTemplate(): Processes Thymeleaf email templates

### 4. Security

#### SecurityConfig.java
- **Purpose**: Configures Spring Security
- **Key Configurations**:
  - Password encoding
  - URL-based security rules
  - Login/logout handling
  - CSRF protection
- **Key Methods**:
  - securityFilterChain(): Defines security rules
  - authenticationProvider(): Configures authentication

### 5. Repositories

#### TaskRepository.java
- **Purpose**: Task data access interface
- **Key Methods**:
  - findByUserId(): Gets user's tasks
  - findByUserIdAndStatus(): Filters tasks by status
  - countByUserIdAndStatus(): Counts tasks by status
  - findLatestTasksByUserId(): Gets recent tasks

#### UserRepository.java
- **Purpose**: User data access interface
- **Key Methods**:
  - findByUsername(): Finds user by username
  - findByEmail(): Finds user by email

### 6. Templates

#### home.html
- **Purpose**: Dashboard template
- **Features**:
  - Task statistics display
  - Task list with filtering
  - Quick actions for tasks

#### task/create.html & edit.html
- **Purpose**: Task management forms
- **Features**:
  - Task input validation
  - Priority and status selection
  - Date picker for due date

#### auth/register.html & login.html
- **Purpose**: Authentication forms
- **Features**:
  - User registration with validation
  - Login form with error handling

## Key Features

### 1. Task Management
- Create, read, update, and delete tasks
- Set task priority and status
- Track task due dates
- Filter tasks by status and priority

### 2. User Management
- User registration and authentication
- Profile management
- Email notification preferences

### 3. Dashboard
- Task statistics overview
- Quick access to recent tasks
- Task status distribution

### 4. Email Notifications
- Due date reminders
- Task status updates
- Customizable notification preferences

### 5. Security
- Password encryption
- Role-based access control
- Session management
- CSRF protection

## Database Schema

### users_table
- id (PK)
- username (unique)
- password
- email (unique)
- first_name
- last_name
- contact_number
- created_at
- updated_at
- email_notifications_enabled

### tasks_table
- id (PK)
- title
- description
- due_date
- status
- priority
- user_id (FK)
- created_at
- updated_at

## Best Practices Implemented

1. **Security**:
   - Password encryption
   - Input validation
   - CSRF protection
   - Session management

2. **Code Organization**:
   - MVC architecture
   - Service layer abstraction
   - Repository pattern
   - Interface-based design

3. **Performance**:
   - Lazy loading for tasks
   - Pagination for large datasets
   - Caching where appropriate
   - Optimized database queries

4. **Maintainability**:
   - Clear package structure
   - Consistent naming conventions
   - Comprehensive documentation
   - Unit tests for critical components

## Future Enhancements

1. Task Categories/Tags
2. Team Collaboration Features
3. File Attachments
4. Advanced Search/Filter
5. Mobile Application
6. API Documentation
7. Performance Monitoring
8. Backup/Restore Functionality

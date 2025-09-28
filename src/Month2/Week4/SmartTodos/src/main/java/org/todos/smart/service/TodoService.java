package org.todos.smart.service;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.todos.smart.model.ApiResponse;
import org.todos.smart.model.Todo;
import org.todos.smart.model.User;
import org.todos.smart.repository.TodoRepository;
import org.todos.smart.repository.UserRepository;
import org.todos.smart.util.ErrorHandler;
import org.todos.smart.util.JacksonUtil;

import java.time.LocalDateTime;
import java.util.UUID;

public class TodoService {
    private final UserRepository userRepository;
    private final TodoRepository todoRepository;
    private String currentUserId;

    public TodoService() {
        this.userRepository = UserRepository.getInstance();
        this.todoRepository = TodoRepository.getInstance();
    }

    // User related stuff.
    public String registerUser(String email, String password) throws IllegalArgumentException {
        return ErrorHandler.executeWithErrorHandling(() -> {
            Preconditions.checkArgument(!Strings.isNullOrEmpty(email), "Email cannot be null or empty");
            Preconditions.checkArgument(!Strings.isNullOrEmpty(password), "Password cannot be null or empty");
            Preconditions.checkArgument(email.contains("@"), "Invalid email format");

            String userId = UUID.randomUUID().toString();
            User user = new User(userId, email, password);
            return userRepository.createUser(user);
        });
    }

    public String loginUser(String email, String password) {
        return ErrorHandler.executeWithErrorHandling(() -> {
            Preconditions.checkArgument(!Strings.isNullOrEmpty(email), "Email cannot be null or empty");
            Preconditions.checkArgument(!Strings.isNullOrEmpty(password), "Password cannot be null or empty");

            String userId = userRepository.findUserId(email);
            Preconditions.checkNotNull(userId, "User not found");

            User user = userRepository.findUserWithId(userId);
            Preconditions.checkArgument(user.getPassword().equals(password), "Invalid credentials");

            this.currentUserId = userId;

            ApiResponse<User> response = ApiResponse.success("Login successful", user);
            return JacksonUtil.stringify(response);
        });
    }

    public String updatePassword(String oldPassword, String newPassword) {
        return ErrorHandler.executeWithErrorHandling(() -> {
            Preconditions.checkArgument(!Strings.isNullOrEmpty(oldPassword), "Old password cannot be null or empty");
            Preconditions.checkArgument(!Strings.isNullOrEmpty(newPassword), "New password cannot be null or empty");
            Preconditions.checkNotNull(currentUserId, "User not logged in");

            User user = userRepository.findUserWithId(currentUserId);
            Preconditions.checkNotNull(user, "User not found");
            Preconditions.checkArgument(user.getPassword().equals(oldPassword), "The old password provided is invalid");

            user.setPassword(newPassword);

            ApiResponse<String> response = ApiResponse.success("Password updated successfully", null);
            return JacksonUtil.stringify(response);
        });
    }

    public String fetchCurrentUser() {
        return ErrorHandler.executeWithErrorHandling(() -> {
            if (currentUserId == null) {
                ApiResponse<String> response = ApiResponse.error("No user logged in");
                return JacksonUtil.stringify(response);
            }

            User user = userRepository.findUserWithId(currentUserId);
            ApiResponse<User> response = ApiResponse.success("Current user info", user);
            return JacksonUtil.stringify(response);
        });
    }

    public String logout() {
        return ErrorHandler.executeWithErrorHandling(() -> {
            this.currentUserId = null;
            ApiResponse<String> response = ApiResponse.success("Logged out successfully", null);
            
            return JacksonUtil.stringify(response);
        });
    }

    // TODO related stuff.
    public String addTodo(String title, String details) {
        return ErrorHandler.executeWithErrorHandling(() -> {
            Preconditions.checkNotNull(currentUserId, "User not logged in");
            Preconditions.checkArgument(!Strings.isNullOrEmpty(title), "Todo title cannot be null or empty");
            String todoId = UUID.randomUUID().toString();
            Todo todo = new Todo(todoId, currentUserId, title, details);

            return todoRepository.createTodo(currentUserId, todo);
        });
    }

    public String updateTodo(String todoId, String title, String details, Boolean completed) {
        return ErrorHandler.executeWithErrorHandling(() -> {
            Preconditions.checkNotNull(currentUserId, "User not logged in");
            Preconditions.checkArgument(!Strings.isNullOrEmpty(todoId), "Todo ID cannot be null or empty");

            Todo todo = todoRepository.findTodoById(todoId);
            Preconditions.checkNotNull(todo, "Todo not found");
            Preconditions.checkArgument(todo.getUserId().equals(currentUserId), "Unauthorized access to todo");

            if (!Strings.isNullOrEmpty(title)) {
                todo.setTitle(title);
            }
            if (details != null) {
                todo.setDetails(details);
            }
            if (completed != null) {
                todo.setCompleted(completed);
            }
            todo.setUpdatedAt(LocalDateTime.now());

            ApiResponse<Todo> response = ApiResponse.success("Todo updated successfully", todo);
            return JacksonUtil.stringify(response);
        });
    }

    public String removeTodo(String todoId) {
        return ErrorHandler.executeWithErrorHandling(() -> {
            Preconditions.checkNotNull(currentUserId, "User not logged in.");
            Preconditions.checkArgument(!Strings.isNullOrEmpty(todoId), "Todo ID cannot be null or empty");
            return todoRepository.removeTodo(currentUserId, todoId);
        });
    }

    public String fetchAllTodos() {
        return ErrorHandler.executeWithErrorHandling(() -> {
            Preconditions.checkNotNull(currentUserId, "User not logged in");
            return todoRepository.fetchAllTodos(currentUserId);
        });
    }

    public String fetchAllActiveTodos() {
        return ErrorHandler.executeWithErrorHandling(() -> {
            Preconditions.checkNotNull(currentUserId, "User not logged in");
            return todoRepository.fetchAllActiveTodos(currentUserId);
        });
    }

    public String fetchAllCompletedTodos() {
        return ErrorHandler.executeWithErrorHandling(() -> {
            Preconditions.checkNotNull(currentUserId, "User not logged in");
            return todoRepository.fetchAllCompletedTodos(currentUserId);
        });
    }

    public String searchTodos(String query, String searchBy) {
        return ErrorHandler.executeWithErrorHandling(() -> {
            Preconditions.checkNotNull(currentUserId, "User not logged in");
            return todoRepository.searchTodos(currentUserId, query, searchBy);
        });
    }
}

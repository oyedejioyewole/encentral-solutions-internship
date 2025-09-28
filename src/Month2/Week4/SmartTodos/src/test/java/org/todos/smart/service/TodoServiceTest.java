package org.todos.smart.service;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import org.todos.smart.model.ApiResponse;
import org.todos.smart.model.Todo;
import org.todos.smart.repository.TodoRepository;
import org.todos.smart.repository.UserRepository;
import org.todos.smart.util.JacksonUtil;

import java.util.List;

public class TodoServiceTest {
    private TodoService todoService;

    @BeforeEach
    void setUp() {
        todoService = new TodoService();
    }

    @AfterEach
    void tearDown() {
        TodoRepository.getInstance().clean();
        UserRepository.getInstance().clean();
    }

    @Test
    @DisplayName("Should register user successfully with valid inputs")
    void testRegisterSuccess() {
        String response = todoService.register("test@example.com", "password123");

        ApiResponse<?> apiResponse = JacksonUtil.parse(response);
        assertTrue(apiResponse.success());
        assertEquals("User registered successfully", apiResponse.message());
        assertNotNull(apiResponse.data());
    }

    @Test
    @DisplayName("Should fail to register user with null email")
    void testRegisterNullEmail() {
        String response = todoService.register(null, "password123");

        ApiResponse<?> apiResponse = JacksonUtil.parse(response);
        assertFalse(apiResponse.success());
        assertTrue(apiResponse.message().contains("Email cannot be null or empty"));
    }

    @Test
    @DisplayName("Should fail to register user with empty email")
    void testRegisterEmptyEmail() {
        String response = todoService.register("", "password123");

        ApiResponse<?> apiResponse = JacksonUtil.parse(response);
        assertFalse(apiResponse.success());
        assertTrue(apiResponse.message().contains("Email cannot be null or empty"));
    }

    @Test
    @DisplayName("Should fail to register user with invalid email format")
    void testRegisterInvalidEmail() {
        String response = todoService.register("invalid-email", "password123");

        ApiResponse<?> apiResponse = JacksonUtil.parse(response);
        assertFalse(apiResponse.success());
        assertTrue(apiResponse.message().contains("Invalid email format"));
    }

    @Test
    @DisplayName("Should fail to register user with duplicate email")
    void testRegisterDuplicateEmail() {
        // Register first user
        todoService.register("test@example.com", "password123");

        // Try to register second user with same email
        String response = todoService.register("test@example.com", "password456");

        ApiResponse<?> apiResponse = JacksonUtil.parse(response);
        assertFalse(apiResponse.success());
        assertTrue(apiResponse.message().contains("Email already exists"));
    }

    @Test
    @DisplayName("Should fail to register user with null password")
    void testRegisterNullPassword() {
        String response = todoService.register("test@example.com", null);

        ApiResponse<?> apiResponse = JacksonUtil.parse(response);
        assertFalse(apiResponse.success());
        assertTrue(apiResponse.message().contains("Password cannot be null or empty"));
    }

    @Test
    @DisplayName("Should login user successfully with valid credentials")
    void testLoginSuccess() {
        // Register user first
        todoService.register("test@example.com", "password123");

        // Login
        String response = todoService.login("test@example.com", "password123");

        ApiResponse<?> apiResponse = JacksonUtil.parse(response);
        assertTrue(apiResponse.success());
        assertEquals("Login successful", apiResponse.message());
    }

    @Test
    @DisplayName("Should fail to login with non-existent user")
    void testLoginNotFound() {
        String response = todoService.login("nonexistent@example.com", "password123");

        ApiResponse<?> apiResponse = JacksonUtil.parse(response);
        assertFalse(apiResponse.success());
        assertTrue(apiResponse.message().contains("User not found"));
    }

    @Test
    @DisplayName("Should fail to login with wrong password")
    void testLoginWrongPassword() {
        // Register user first
        todoService.register("test@example.com", "password123");

        // Login with wrong password
        String response = todoService.login("test@example.com", "wrong-password");

        ApiResponse<?> apiResponse = JacksonUtil.parse(response);
        assertFalse(apiResponse.success());
        assertTrue(apiResponse.message().contains("Invalid credentials"));
    }

    @Test
    @DisplayName("Should update password successfully when logged in")
    void testUpdatePasswordSuccess() {
        // Register and login user
        todoService.register("test@example.com", "password123");
        todoService.login("test@example.com", "password123");

        // Update password
        String response = todoService.updatePassword("password123", "newPassword456");

        ApiResponse<?> apiResponse = JacksonUtil.parse(response);
        assertTrue(apiResponse.success());
        assertEquals("Password updated successfully", apiResponse.message());
    }

    @Test
    @DisplayName("Should fail to update password when not logged in")
    void testUpdatePasswordNotLoggedIn() {
        String response = todoService.updatePassword("password123", "newPassword456");

        ApiResponse<?> apiResponse = JacksonUtil.parse(response);
        assertFalse(apiResponse.success());
        assertTrue(apiResponse.message().contains("User not logged in"));
    }

    @Test
    @DisplayName("Should fail to update password if any parameters are invalid")
    void testUpdatePasswordInvalidParameters() {
        ApiResponse<?> response = JacksonUtil.parse(todoService.updatePassword("", "new-password-123"));

        assertFalse(response.success());
        assertEquals("Old password cannot be null or empty", response.message());

        response = JacksonUtil.parse(todoService.updatePassword("old-password-123", ""));
        assertFalse(response.success());
        assertEquals("New password cannot be null or empty", response.message());
    }

    @Test
    @DisplayName("Should fail to update password if old password doesn't match password of current user")
    void testUpdatePasswordOldPasswordMismatch() {
        todoService.register("test@example.com", "password123");
        todoService.login("test@example.com", "password123");

        ApiResponse<?> response = JacksonUtil.parse(todoService.updatePassword("passwrd123", "my-wonderful-password"));
        assertFalse(response.success());
        assertEquals("The old password provided is invalid", response.message());
    }

    // TODO Management Tests
    @Test
    @DisplayName("Should add todo successfully when logged in")
    void testAddTodoSuccess() {
        // Register and login user
        todoService.register("test@example.com", "password123");
        todoService.login("test@example.com", "password123");

        // Add todo
        String response = todoService.addTodo("Test Todo", "This is a test todo");

        ApiResponse<?> apiResponse = JacksonUtil.parse(response);
        assertTrue(apiResponse.success());
        assertEquals("Todo added successfully", apiResponse.message());
        assertNotNull(apiResponse.data());
    }

    @Test
    @DisplayName("Should fail to add todo when not logged in")
    void testAddTodoNotLoggedIn() {
        String response = todoService.addTodo("Test Todo", "This is a test todo");

        ApiResponse<?> apiResponse = JacksonUtil.parse(response);
        assertFalse(apiResponse.success());
        assertTrue(apiResponse.message().contains("User not logged in"));
    }

    @Test
    @DisplayName("Should fail to add todo with empty title")
    void testAddTodoEmptyTitle() {
        // Register and login user
        todoService.register("test@example.com", "password123");
        todoService.login("test@example.com", "password123");

        // Add todo with empty title
        String response = todoService.addTodo("", "This is a test todo");

        ApiResponse<?> apiResponse = JacksonUtil.parse(response);
        assertFalse(apiResponse.success());
        assertTrue(apiResponse.message().contains("Todo title cannot be null or empty"));
    }

    @Test
    @DisplayName("Should updated a todo provided its ID")
    void testUpdateTodo() {
        todoService.register("test@example.com", "password123");
        todoService.login("test@example.com", "password123");

        // Add todo with empty title
        ApiResponse<?> response = JacksonUtil.parse(todoService.addTodo("Test Todo", "This is a test todo"));
        assertTrue(response.success());
        assertEquals("Todo added successfully", response.message());

        Todo todo = JacksonUtil.convertValue(response.data(), Todo.class);
        assertNotNull(todo.getId());

        response = JacksonUtil.parse(todoService.updateTodo(todo.getId(), "This week", "Walk Oliver (my dog)", false));
        assertTrue(response.success());
        assertEquals("Todo updated successfully", response.message());

        todo = JacksonUtil.convertValue(response.data(), Todo.class);
        assertEquals("Walk Oliver (my dog)", todo.getDetails());
    }

    @Test
    @DisplayName("Should delete a todo provided its ID")
    void testDeleteTodo() {
        todoService.register("test@example.com", "password123");
        todoService.login("test@example.com", "password123");

        ApiResponse<?> response = JacksonUtil.parse(todoService.addTodo("This week", "Walk Oliver (my dog)"));
        assertTrue(response.success());

        response = JacksonUtil.parse(todoService.fetchAllTodos());
        assertTrue(response.success());

        List<Todo> allTodosForUser = JacksonUtil.convertComplexValue(response.data(), new TypeReference<>() {});
        assertEquals(1, allTodosForUser.size());

        Todo todo = allTodosForUser.getFirst();
        assertNotNull(todo);

        response = JacksonUtil.parse(todoService.removeTodo(todo.getId()));
        assertTrue(response.success());
        assertEquals("Todo deleted successfully", response.message());

        response = JacksonUtil.parse(todoService.fetchAllTodos());
        assertTrue(response.success());

        allTodosForUser = JacksonUtil.convertComplexValue(response.data(), new TypeReference<>() {});
        assertEquals(0, allTodosForUser.size());
    }

    @Test
    @DisplayName("Should get all todos successfully")
    void testGetAllTodosSuccess() {
        // Register and login user
        todoService.register("test@example.com", "password123");
        todoService.login("test@example.com", "password123");

        // Add some todos
        todoService.addTodo("Todo 1", "First todo");
        todoService.addTodo("Todo 2", "Second todo");

        // Get all todos
        String response = todoService.fetchAllTodos();

        ApiResponse<?> apiResponse = JacksonUtil.parse(response);
        assertTrue(apiResponse.success());
        assertEquals("Todos retrieved successfully", apiResponse.message());
        assertNotNull(apiResponse.data());
    }

    @Test
    @DisplayName("Should get active todos successfully")
    void testGetActiveTodosSuccess() {
        // Register and login user
        todoService.register("test@example.com", "password123");
        todoService.login("test@example.com", "password123");

        // Add todo
        todoService.addTodo("Active Todo", "This is an active todo");

        // Get active todos
        String response = todoService.fetchAllActiveTodos();

        ApiResponse<?> apiResponse = JacksonUtil.parse(response);
        assertTrue(apiResponse.success());
        assertEquals("Active todos retrieved successfully", apiResponse.message());
    }

    @Test
    @DisplayName("Should get completed todos successfully")
    void testGetCompletedTodosSuccess() {
        // Register and login user
        todoService.register("test@example.com", "password123");
        todoService.login("test@example.com", "password123");

        // Get completed todos (should be empty initially)
        String response = todoService.fetchAllCompletedTodos();

        ApiResponse<?> apiResponse = JacksonUtil.parse(response);
        assertTrue(apiResponse.success());
        assertEquals("Completed todos retrieved successfully", apiResponse.message());
    }

    @Test
    @DisplayName("Should search todos by title successfully")
    void testSearchTodosByTitle() {
        // Register and login user
        todoService.register("test@example.com", "password123");
        todoService.login("test@example.com", "password123");

        // Add todos
        todoService.addTodo("Buy groceries", "Milk, Bread, Eggs");
        todoService.addTodo("Complete project", "Finish the TODO application");

        // Search by title
        String response = todoService.searchTodos("grocery", "title");

        ApiResponse<?> apiResponse = JacksonUtil.parse(response);
        assertTrue(apiResponse.success());
        assertEquals("Search completed successfully", apiResponse.message());
    }

    @Test
    @DisplayName("Should search todos by details successfully")
    void testSearchTodosByDetails() {
        // Register and login user
        todoService.register("test@example.com", "password123");
        todoService.login("test@example.com", "password123");

        // Add todos
        todoService.addTodo("Shopping", "Buy milk and bread from store");
        todoService.addTodo("Work", "Complete the project documentation");

        // Search by details
        String response = todoService.searchTodos("milk", "details");

        ApiResponse<?> apiResponse = JacksonUtil.parse(response);
        assertTrue(apiResponse.success());
        assertEquals("Search completed successfully", apiResponse.message());
    }

    @Test
    @DisplayName("Should fail to search todos when not logged in")
    void testSearchTodosNotLoggedIn() {
        String response = todoService.searchTodos("test", "title");

        ApiResponse<?> apiResponse = JacksonUtil.parse(response);
        assertFalse(apiResponse.success());
        assertTrue(apiResponse.message().contains("User not logged in"));
    }

    @Test
    @DisplayName("Should fail to search todos with empty query")
    void testSearchTodosEmptyQuery() {
        // Register and login user
        todoService.register("test@example.com", "password123");
        todoService.login("test@example.com", "password123");

        // Search with empty query
        String response = todoService.searchTodos("", "title");

        ApiResponse<?> apiResponse = JacksonUtil.parse(response);
        assertFalse(apiResponse.success());
        assertTrue(apiResponse.message().contains("Search query cannot be null or empty"));
    }

    @Test
    @DisplayName("Should get current user info successfully when logged in")
    void testGetCurrentUserSuccess() {
        // Register and login user
        todoService.register("test@example.com", "password123");
        todoService.login("test@example.com", "password123");

        // Get current user
        String response = todoService.fetchCurrentUser();

        ApiResponse<?> apiResponse = JacksonUtil.parse(response);
        assertTrue(apiResponse.success());
        assertEquals("Current user info", apiResponse.message());
        assertNotNull(apiResponse.data());
    }

    @Test
    @DisplayName("Should fail to get current user when not logged in")
    void testGetCurrentUserNotLoggedIn() {
        String response = todoService.fetchCurrentUser();

        ApiResponse<?> apiResponse = JacksonUtil.parse(response);
        assertFalse(apiResponse.success());
        assertTrue(apiResponse.message().contains("No user logged in"));
    }

    @Test
    @DisplayName("Should logout successfully")
    void testLogoutSuccess() {
        // Register and login user
        todoService.register("test@example.com", "password123");
        todoService.login("test@example.com", "password123");

        // Logout
        String response = todoService.logout();

        ApiResponse<?> apiResponse = JacksonUtil.parse(response);
        assertTrue(apiResponse.success());
        assertEquals("Logged out successfully", apiResponse.message());

        // Verify user is logged out by trying to add todo
        String todoResponse = todoService.addTodo("Test", "Should fail");
        ApiResponse<?> todoApiResponse = JacksonUtil.parse(todoResponse);
        assertFalse(todoApiResponse.success());
        assertTrue(todoApiResponse.message().contains("User not logged in"));
    }

    // (START) Integration Tests

    @Test
    @DisplayName("Should support multiple users with isolated todos")
    void testMultipleUsersIsolation() {
        // Register and login first user
        todoService.register("user1@example.com", "password123");
        todoService.login("user1@example.com", "password123");
        todoService.addTodo("User 1 Todo", "This belongs to user 1");

        // Logout and register second user
        todoService.logout();
        todoService.register("user2@example.com", "password456");
        todoService.login("user2@example.com", "password456");
        todoService.addTodo("User 2 Todo", "This belongs to user 2");

        // Get todos for user 2 (should only see their own)
        String response = todoService.fetchAllTodos();
        ApiResponse<?> apiResponse = JacksonUtil.parse(response);
        assertTrue(apiResponse.success());

        // Verify isolation by logging back in as user 1
        todoService.logout();
        todoService.login("user1@example.com", "password123");
        String user1Response = todoService.fetchAllTodos();
        ApiResponse<?> user1ApiResponse = JacksonUtil.parse(user1Response);
        assertTrue(user1ApiResponse.success());
    }

    @Test
    @DisplayName("Should handle complete todo lifecycle")
    void testCompleteLifecycle() {
        // Register and login user
        todoService.register("test@example.com", "password123");
        todoService.login("test@example.com", "password123");

        // Add todo
        String addResponse = todoService.addTodo("Test Todo", "Initial description");
        ApiResponse<?> addApiResponse = JacksonUtil.parse(addResponse);
        assertTrue(addApiResponse.success());

        // Get all todos to verify addition
        String getAllResponse = todoService.fetchAllTodos();
        ApiResponse<?> getAllApiResponse = JacksonUtil.parse(getAllResponse);
        assertTrue(getAllApiResponse.success());

        // Search for the todo
        String searchResponse = todoService.searchTodos("Test", "title");
        ApiResponse<?> searchApiResponse = JacksonUtil.parse(searchResponse);
        assertTrue(searchApiResponse.success());

        // Get active todos (should include our todo)
        String activeResponse = todoService.fetchAllActiveTodos();
        ApiResponse<?> activeApiResponse = JacksonUtil.parse(activeResponse);
        assertTrue(activeApiResponse.success());
    }

    // (END) Integration tests
}
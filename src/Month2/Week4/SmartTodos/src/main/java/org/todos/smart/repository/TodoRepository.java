package org.todos.smart.repository;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.todos.smart.model.ApiResponse;
import org.todos.smart.model.Todo;
import org.todos.smart.util.ErrorHandler;
import org.todos.smart.util.JacksonUtil;

import java.time.format.DateTimeFormatter;
import java.util.*;

public class TodoRepository {
    private final MultiValuedMap<String, Todo> userTodos;
    private final Map<String, Todo> allTodos;

    private static TodoRepository instance;

    public TodoRepository() {
        this.userTodos = new ArrayListValuedHashMap<>();
        this.allTodos = new HashMap<>();
    }

    public static TodoRepository getInstance() {
        if (instance == null) {
            instance = new TodoRepository();
        }

        return instance;
    }

    public String createTodo(String currentUserId, Todo todo) {
        return ErrorHandler.executeWithErrorHandling(() -> {
            userTodos.put(currentUserId, todo);
            allTodos.put(todo.getId(), todo);

            ApiResponse<Todo> response = ApiResponse.success("Todo added successfully", todo);
            return JacksonUtil.stringify(response);
        });
    }

    public String removeTodo(String currentUserId, String todoId) {
        return ErrorHandler.executeWithErrorHandling(() -> {
            Todo todo = this.findTodoById(todoId);
            Preconditions.checkNotNull(todo, "Todo not found");
            Preconditions.checkArgument(todo.getUserId().equals(currentUserId), "Unauthorized access to todo.");

            userTodos.removeMapping(currentUserId, todo);
            allTodos.remove(todoId);

            ApiResponse<String> response = ApiResponse.success("Todo deleted successfully", null);
            return JacksonUtil.stringify(response);
        });
    }

    public Todo findTodoById(String todoId) {
        return allTodos.get(todoId);
    }

    public Collection<Todo> findTodosForCurrentUser(String currentUserId) {
        return userTodos.get(currentUserId);
    }

    public String fetchAllTodos(String currentUserId) {
        return ErrorHandler.executeWithErrorHandling(() -> {
            Collection<Todo> todos = this.findTodosForCurrentUser(currentUserId);
            List<Todo> todoList = Lists.newArrayList(todos); // Using Guava Lists

            ApiResponse<List<Todo>> response = ApiResponse.success("Todos retrieved successfully", todoList);
            return JacksonUtil.stringify(response);
        });
    }

    public String fetchAllActiveTodos(String currentUserId) {
        return ErrorHandler.executeWithErrorHandling(() -> {
            Collection<Todo> todos = this.findTodosForCurrentUser(currentUserId);
            // Using CollectionUtils from Apache Commons Collections for filtering
            Collection<Todo> activeTodos = CollectionUtils.select(todos, todo -> !todo.isCompleted());
            List<Todo> activeTodoList = Lists.newArrayList(activeTodos);

            ApiResponse<List<Todo>> response = ApiResponse.success("Active todos retrieved successfully", activeTodoList);
            return JacksonUtil.stringify(response);
        });
    }

    public String fetchAllCompletedTodos(String currentUserId) {
        return ErrorHandler.executeWithErrorHandling(() -> {
            Collection<Todo> todos = this.findTodosForCurrentUser(currentUserId);
            // Using CollectionUtils for filtering
            Collection<Todo> completedTodos = CollectionUtils.select(todos, Todo::isCompleted);
            List<Todo> completedTodoList = Lists.newArrayList(completedTodos);

            ApiResponse<List<Todo>> response = ApiResponse.success("Completed todos retrieved successfully", completedTodoList);
            return JacksonUtil.stringify(response);
        });
    }

    public String searchTodos(String currentUserId, String query, String searchBy) {
        return ErrorHandler.executeWithErrorHandling(() -> {
            Preconditions.checkArgument(!Strings.isNullOrEmpty(query), "Search query cannot be null or empty");

            Collection<Todo> todos = userTodos.get(currentUserId);
            List<Todo> searchResults = Lists.newArrayList();

            if (Strings.isNullOrEmpty(searchBy) || "title".equalsIgnoreCase(searchBy)) {
                // Search by title
                Collection<Todo> titleMatches = CollectionUtils.select(todos,
                        todo -> todo.getTitle().toLowerCase().contains(query.toLowerCase()));
                searchResults.addAll(titleMatches);
            }

            if (Strings.isNullOrEmpty(searchBy) || "details".equalsIgnoreCase(searchBy)) {
                // Search by details
                Collection<Todo> detailMatches = CollectionUtils.select(todos,
                        todo -> todo.getDetails() != null && todo.getDetails().toLowerCase().contains(query.toLowerCase()));
                searchResults.addAll(detailMatches);
            }

            if (Strings.isNullOrEmpty(searchBy) || "date".equalsIgnoreCase(searchBy)) {
                // Search by creation date (formatted as string)
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                Collection<Todo> dateMatches = CollectionUtils.select(todos,
                        todo -> todo.getCreatedAt().format(formatter).contains(query));
                searchResults.addAll(dateMatches);
            }

            // Remove duplicates using Guava Sets
            Set<Todo> uniqueResults = Sets.newLinkedHashSet(searchResults);
            List<Todo> finalResults = Lists.newArrayList(uniqueResults);

            ApiResponse<List<Todo>> response = ApiResponse.success("Search completed successfully", finalResults);
            return JacksonUtil.stringify(response);
        });
    }

    public void clean() {
        userTodos.clear();
        allTodos.clear();
    }
}

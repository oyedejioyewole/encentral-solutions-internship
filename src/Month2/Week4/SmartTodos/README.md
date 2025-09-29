# Introducing SmartTodos

This is a comprehensive TODO application implemented in Java using Guava, Jackson, and Apache Commons Collections. This application allows users to register, log in, and manage their personal to-do lists with full CRUD operations and search functionality.

## Demonstration

https://github.com/user-attachments/assets/97b0301f-5506-4579-97fd-e1cb39b69928

## Features

### User Management
- **User Registration**: Create new user accounts with unique email addresses (also mapped to a user's ID)
- **User Login**: Authenticate users with email and password.
- **Password Update**: Allow logged-in users to change their passwords, requiring their `currentPassword` to be valid.
- **Session Management**: Simple session-based user state management.

### TODO Management (requires the user to be logged in)
- **Add TODO**: Create new todo items with title and details
- **Update TODO**: Modify existing todos (title, details, completion status).
- **Delete TODO**: Remove the todo from the system.
- **View All TODOs**: Get all todos.
- **Filter by Status**: Get only active or completed todos.
- **Search TODOs**: Search todos by title, details, or creation date.

## Technical Implementation

### Required Libraries Usage

#### 1. Google Guava
- **Preconditions**: Used extensively for input validation throughout the application
    - Validates non-null/non-empty inputs
    - Ensures business logic constraints (unique emails, authorised access)
- **Collection Utilities**:
    - `Lists.newArrayList()` for creating lists
    - `Sets.newLinkedHashSet()` for removing duplicates in search results

#### 2. Jackson (JSON Processing)
- **Object to JSON Conversion**: All API responses are converted to JSON using Jackson through a `JacksonUtil` helper.
- **DateTime Support**: Configured with JavaTimeModule for LocalDateTime serialization.
- **Annotations**: Used `@JsonProperty` for consistent JSON field mapping.

#### 3. Apache Commons Collections
- **BidiMap**: `DualHashBidiMap` ensures unique email addresses and provides bidirectional mapping.
- **MultiValuedMap**: `ArrayListValuedHashMap` maps users to their multiple todos.
- **CollectionUtils**: Used for filtering operations (active/completed todos, search functionality)

### Architecture & Design Decisions

#### Storage Strategy
The application uses in-memory storage with the following structure:
- `BidiMap<String, String>` - Maps email to userId (ensures unique emails)
- `Map<String, User>` - Stores user objects by userId.
- `MultiValuedMap<String, Todo>` - Maps userId to their todos (one-to-many relationship)
- `Map<String, Todo>` - Global todo lookup by todoId.

#### Session Management
- `TodoService` is a singleton to persist state across all instances.
- Simple session management using a `currentUserId` field.
- Users must log in to perform todo operations.
- Session persists until logout or application restart.

#### UUID Generation
- Uses `UUID.randomUUID().toString()` for generating unique IDs for users and todos
- Ensures uniqueness across all entities

#### Search Strategy
The search functionality supports multiple search criteria:
- **By Title**: Case-insensitive partial matching
- **By Details**: Case-insensitive partial matching in todo descriptions
- **By Date**: Searches formatted creation date (yyyy-MM-dd format)
- **Combined Search**: When no specific field is specified, searches across all fields

#### Error Handling
- All methods return JSON responses with success/error status
- Input validation using Guava Preconditions with descriptive error messages
- Consistent error response format across all endpoints

## API Usage Examples

### User Operations

#### Register a new user
```java
TodoService service = new TodoService();
String response = service.register("user@example.com", "password123");
// Returns: {"success":true, "message": "User registered successfully", "data":{...}}
```

#### Login
```java
String response = service.login("user@example.com", "password123");
// Returns: {"success":true,"message":"Login successful","data":{...}}
```

#### Logout
```java
String response = service.logout();
// Returns: {"success":true,"message":"Logged out successfully","data":{...}}
```

#### Update Password
```java
String response = service.updatePassword("oldPassword123", "newPassword456");
// Returns: {"success":true,"message":"Password updated successfully","data":null}
```

### TODO Operations

#### Add a new TODO
```java
String response = service.createTodo("Buy groceries", "Milk, Bread, Eggs, Fruits");
// Returns: {"success":true,"message":"Todo added successfully","data":{...}}
```

#### Update a TODO
```java
String response = service.updateTodo("todo-id", "Updated title", "Updated details", true);
// Returns: {"success":true,"message":"Todo updated successfully","data":{...}}
```

#### Get all TODOs
```java
String response = service.fetchAllTodos();
// Returns: {"success":true,"message":"Todos retrieved successfully","data":[...]}
```

#### Get active TODOs only
```java
String response = service.fetchAllActiveTodos();
// Returns: {"success":true,"message":"Active todos retrieved successfully","data":[...]}
```

#### Get completed TODOs only
```java
String response = service.fetchAllCompletedTodos();
// Returns: {"success":true,"message":"Active todos retrieved successfully","data":[...]}
```

#### Search TODOs
```java
String response = service.searchTodos("grocery", "title");
// Returns: {"success":true,"message":"Search completed successfully","data":[...]}
```

#### Delete a TODO
```java
String response = service.removeTodo("todo-id");
// Returns: {"success":true,"message":"Todo deleted successfully","data":null}
```

## Response Format

All API responses follow a consistent JSON format:

### Success Response
```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": {
    // Response data (can be object, array, or null)
  }
}
```

### Error Response
```json
{
  "success": false,
  "message": "Error description",
  "data": null
}
```

## Constraints & Validations

### User Constraints
- Email must be unique across all users
- Email must contain '@' symbol (basic format validation)
- Email and password cannot be null or empty
- Users must be logged in to perform todo operations

### TODO Constraints
- Todo title cannot be null or empty
- Users can only access their own todos
- Todo IDs must be valid and exist in the system
- Only the todo owner can modify or delete todos

## Technical Highlights

### Library Integration
1. **Guava Integration**
    - Extensive use of `Preconditions.checkArgument()` and `Preconditions.checkNotNull()` for validation
    - Collection utilities for data manipulation
    - Ensures clean, readable validation code

2. **Jackson Integration**
    - Automatic JSON serialization/deserialization
    - Custom datetime handling with JavaTimeModule
    - Consistent API response format

3. **Apache Commons Collections Integration**
    - BidiMap for ensuring unique email constraints
    - MultiValuedMap for efficient user-to-todos mapping
    - CollectionUtils for filtering and searching operations

### Performance Considerations
- In-memory storage for fast access
- Efficient data structures for O(1) lookups where possible
- Minimal object creation during operations

### Security Considerations
- Password storage (Note: In production, passwords should be hashed)
- User isolation (users can only access their own todos)
- Input validation to prevent malicious data

## Assumptions Made

1. **Session Management**: Simple in-memory session using currentUserId field
2. **Password Security**: Passwords stored in plain text (not production-ready)
3. **Data Persistence**: All data is stored in memory and lost on restart
4. **Search Functionality**: Case-insensitive partial matching for text fields
5. **Date Search**: Searches formatted date strings (yyyy-MM-dd)
6. **Error Handling**: All errors return JSON responses with descriptive messages
7. **UUID Generation**: Using Java's built-in UUID for unique identifiers
8. **Concurrency**: Single-threaded operation assumed (no thread safety implemented)

## Dependencies

- **Google Guava 33.5.0-jre**: Preconditions, collection utilities
- **Jackson 2.19.2**: JSON processing and datetime handling
- **Apache Commons Collections 4.5**: Specialized collections (BidiMap, MultiValuedMap)
- **JUnit 5.13.4**: Testing framework (for future test implementation)

---

**Note**: This is a demonstration application. For production use, consider implementing proper security measures, persistent storage, and additional error handling.

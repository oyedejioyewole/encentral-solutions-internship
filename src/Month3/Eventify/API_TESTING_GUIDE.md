# Eventify API Testing Guide

This guide provides sample requests to test all endpoints of the Eventify API.

## Prerequisites

1. Start the application: `./mvnw spring-boot:run`
2. Application runs on: `http://localhost:8080`
3. Save your token after signup/login for authenticated requests

---

## 1. Authentication Endpoints

### 1.1 Sign Up (Register New User)

**Endpoint**: `POST /api/auth/signup`

**Request Body**:
```json
{
  "name": "Alice Johnson",
  "email": "alice@example.com",
  "password": "password123"
}
```

**Expected Response** (201 Created):
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "userId": "...",
  "name": "Alice Johnson",
  "email": "alice@example.com"
}
```

**Save the token** for subsequent requests!

---

### 1.2 Login

**Endpoint**: `POST /api/auth/login`

**Request Body**:
```json
{
  "email": "alice@example.com",
  "password": "password123"
}
```

**Expected Response** (200 OK):
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "userId": "...",
  "name": "Alice Johnson",
  "email": "alice@example.com"
}
```

---

## 2. Event Endpoints

**Note**: All event endpoints require authentication. Add header:
```
Authorization: Bearer JWT_TOKEN_HERE
```

### 2.1 Create Event

**Endpoint**: `POST /api/events`

**Headers**:
```
Authorization: Bearer JWT_TOKEN_HERE
Content-Type: application/json
```

**Request Body**:
```json
{
  "title": "Tech Conference 2025",
  "description": "Annual technology and innovation conference featuring industry leaders",
  "eventDate": "2025-12-15T09:00:00",
  "location": "San Francisco Convention Center, CA"
}
```

**Expected Response** (201 Created):
```json
{
  "id": "...",
  "title": "Tech Conference 2025",
  "description": "Annual technology and innovation conference featuring industry leaders",
  "eventDate": "2025-12-15T09:00:00",
  "location": "San Francisco Convention Center, CA",
  "createdAt": "2025-10-11T10:30:00",
  "updatedAt": "2025-10-11T10:30:00",
  "participantCount": 0
}
```

---

### 2.2 Create Another Event

**Endpoint**: `POST /api/events`

**Request Body**:
```json
{
  "title": "Spring Boot Workshop",
  "description": "Hands-on workshop covering Spring Boot best practices",
  "eventDate": "2025-11-20T14:00:00",
  "location": "Online (Zoom)"
}
```

---

### 2.3 Get All Events (Paginated)

**Endpoint**: `GET /api/events?page=0&size=10&sort=eventDate,asc`

**Headers**:
```
Authorization: Bearer JWT_TOKEN_HERE
```

**Expected Response** (200 OK):
```json
{
  "content": [
    {
      "id": "...",
      "title": "Spring Boot Workshop",
      "description": "Hands-on workshop covering Spring Boot best practices",
      "eventDate": "2025-11-20T14:00:00",
      "location": "Online (Zoom)",
      "createdAt": "2025-10-11T10:31:00",
      "updatedAt": "2025-10-11T10:31:00",
      "participantCount": 0
    },
    {
      "id": "...",
      "title": "Tech Conference 2025",
      "description": "Annual technology and innovation conference featuring industry leaders",
      "eventDate": "2025-12-15T09:00:00",
      "location": "San Francisco Convention Center, CA",
      "createdAt": "2025-10-11T10:30:00",
      "updatedAt": "2025-10-11T10:30:00",
      "participantCount": 0
    }
  ],
  "pageable": {
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    },
    "pageNumber": 0,
    "pageSize": 10,
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalPages": 1,
  "totalElements": 2,
  "last": true,
  "size": 10,
  "number": 0,
  "sort": {
    "sorted": true,
    "unsorted": false,
    "empty": false
  },
  "numberOfElements": 2,
  "first": true,
  "empty": false
}
```

---

### 2.4 Get Event by ID

**Endpoint**: `GET /api/events/{eventId}`

**Headers**:
```
Authorization: Bearer JWT_TOKEN_HERE
```

**Expected Response** (200 OK):
```json
{
  "id": "...",
  "title": "Tech Conference 2025",
  "description": "Annual technology and innovation conference featuring industry leaders",
  "eventDate": "2025-12-15T09:00:00",
  "location": "San Francisco Convention Center, CA",
  "createdAt": "2025-10-11T10:30:00",
  "updatedAt": "2025-10-11T10:30:00",
  "participantCount": 0
}
```

---

### 2.5 Update Event (Full Update - PUT)

**Endpoint**: `PUT /api/events/{eventId}`

**Headers**:
```
Authorization: Bearer JWT_TOKEN_HERE
Content-Type: application/json
```

**Request Body**:
```json
{
  "title": "Tech Conference 2025 - Updated",
  "description": "Annual technology and innovation conference with new speakers",
  "eventDate": "2025-12-16T09:00:00",
  "location": "San Francisco Convention Center, CA"
}
```

**Expected Response** (200 OK):
```json
{
  "id": "...",
  "title": "Tech Conference 2025 - Updated",
  "description": "Annual technology and innovation conference with new speakers",
  "eventDate": "2025-12-16T09:00:00",
  "location": "San Francisco Convention Center, CA",
  "createdAt": "2025-10-11T10:30:00",
  "updatedAt": "2025-10-11T10:35:00",
  "participantCount": 0
}
```

---

### 2.6 Partial Update Event (PATCH)

**Endpoint**: `PATCH /api/events/{eventId}`

**Headers**:
```
Authorization: Bearer JWT_TOKEN_HERE
Content-Type: application/json
```

**Request Body** (only update location):
```json
{
  "location": "Virtual Event - Zoom"
}
```

**Expected Response** (200 OK):
```json
{
  "id": "...",
  "title": "Tech Conference 2025 - Updated",
  "description": "Annual technology and innovation conference with new speakers",
  "eventDate": "2025-12-16T09:00:00",
  "location": "Virtual Event - Zoom",
  "createdAt": "2025-10-11T10:30:00",
  "updatedAt": "2025-10-11T10:36:00",
  "participantCount": 0
}
```

---

### 2.7 Search Events

**Endpoint**: `GET /api/events/search?keyword=conference&page=0&size=10`

**Headers**:
```
Authorization: Bearer JWT_TOKEN_HERE
```

**Expected Response** (200 OK):
```json
{
  "content": [
    {
      "id": "...",
      "title": "Tech Conference 2025 - Updated",
      "description": "Annual technology and innovation conference with new speakers",
      "eventDate": "2025-12-16T09:00:00",
      "location": "Virtual Event - Zoom",
      "createdAt": "2025-10-11T10:30:00",
      "updatedAt": "2025-10-11T10:36:00",
      "participantCount": 0
    }
  ],
  "totalPages": 1,
  "totalElements": 1,
  "size": 10,
  "number": 0
}
```

---

### 2.8 Delete Event

**Endpoint**: `DELETE /api/events/{eventId}`

**Headers**:
```
Authorization: Bearer JWT_TOKEN_HERE
```

**Expected Response** (204 No Content)

---

## 3. Participant Endpoints

**Note**: All participant endpoints require authentication and event ownership.

### 3.1 Upload Participants (CSV)

**Endpoint**: `POST /api/events/{eventId}/participants/upload`

**Headers**:
```
Authorization: Bearer JWT_TOKEN_HERE
Content-Type: multipart/form-data
```

**Form Data**:
- Key: `file`
- Value: Select CSV file

**Sample CSV Content** (`participants.csv`):
```csv
name,email,phone
John Doe,john.doe@example.com,+1234567890
Jane Smith,jane.smith@example.com,+0987654321
Bob Wilson,bob.wilson@example.com,+1122334455
Alice Brown,alice.brown@example.com,
```

**Expected Response** (201 Created):
```json
[
  {
    "id": "...",
    "name": "John Doe",
    "email": "john.doe@example.com",
    "phone": "+1234567890",
    "invitationStatus": "PENDING",
    "eventId": "...",
    "createdAt": "2025-10-11T10:40:00"
  },
  {
    "id": "...",
    "name": "Jane Smith",
    "email": "jane.smith@example.com",
    "phone": "+0987654321",
    "invitationStatus": "PENDING",
    "eventId": "...",
    "createdAt": "2025-10-11T10:40:00"
  },
  {
    "id": "...",
    "name": "Bob Wilson",
    "email": "bob.wilson@example.com",
    "phone": "+1122334455",
    "invitationStatus": "PENDING",
    "eventId": "...",
    "createdAt": "2025-10-11T10:40:00"
  },
  {
    "id": "...",
    "name": "Alice Brown",
    "email": "alice.brown@example.com",
    "phone": null,
    "invitationStatus": "PENDING",
    "eventId": "...",
    "createdAt": "2025-10-11T10:40:00"
  }
]
```

---

### 3.3 Get All Participants (Paginated)

**Endpoint**: `GET /api/events/{eventId}/participants?page=0&size=20&sort=createdAt,desc`

**Headers**:
```
Authorization: Bearer JWT_TOKEN_HERE
```

**Expected Response** (200 OK):
```json
{
  "content": [
    {
      "id": "...",
      "name": "Alice Brown",
      "email": "alice.brown@example.com",
      "phone": null,
      "invitationStatus": "PENDING",
      "eventId": "...",
      "createdAt": "2025-10-11T10:40:00"
    },
    {
      "id": "...",
      "name": "Bob Wilson",
      "email": "bob.wilson@example.com",
      "phone": "+1122334455",
      "invitationStatus": "PENDING",
      "eventId": "...",
      "createdAt": "2025-10-11T10:40:00"
    },
    {
      "id": "...",
      "name": "Jane Smith",
      "email": "jane.smith@example.com",
      "phone": "+0987654321",
      "invitationStatus": "PENDING",
      "eventId": "...",
      "createdAt": "2025-10-11T10:40:00"
    },
    {
      "id": "...",
      "name": "John Doe",
      "email": "john.doe@example.com",
      "phone": "+1234567890",
      "invitationStatus": "PENDING",
      "eventId": "...",
      "createdAt": "2025-10-11T10:40:00"
    }
  ],
  "totalPages": 1,
  "totalElements": 4,
  "size": 20,
  "number": 0
}
```

---

### 3.4 Update Participant Status to ACCEPTED

**Endpoint**: `PATCH /api/events/1/participants/{participantId}/status?status=ACCEPTED`

**Headers**:
```
Authorization: Bearer JWT_TOKEN_HERE
```

**Expected Response** (200 OK):
```json
{
  "id": "...",
  "name": "John Doe",
  "email": "john.doe@example.com",
  "phone": "+1234567890",
  "invitationStatus": "ACCEPTED",
  "eventId": "...",
  "createdAt": "2025-10-11T10:40:00"
}
```

---

### 3.5 Update Participant Status to DECLINED

**Endpoint**: `PATCH /api/events/{eventId}/participants/{participantId}/status?status=DECLINED`

**Headers**:
```
Authorization: Bearer JWT_TOKEN_HERE
```

**Expected Response** (200 OK):
```json
{
  "id": "...",
  "name": "Jane Smith",
  "email": "jane.smith@example.com",
  "phone": "+0987654321",
  "invitationStatus": "DECLINED",
  "eventId": "...",
  "createdAt": "2025-10-11T10:40:00"
}
```

---

## 4. Testing Authorization (User Isolation)

### 4.1 Create Second User

**Endpoint**: `POST /api/auth/signup`

**Request Body**:
```json
{
  "name": "Bob Martin",
  "email": "bob@example.com",
  "password": "password456"
}
```

**Save the new token** as `TOKEN_USER2`

---

### 4.2 Try to Access User 1's Event with User 2's Token

**Endpoint**: `GET /api/events/{eventId}`

**Headers**:
```
Authorization: Bearer TOKEN_USER2
```

**Expected Response** (403 Forbidden):
```json
{
  "status": 403,
  "message": "You do not have permission to access this event",
  "timestamp": "2025-10-11T10:45:00"
}
```

This confirms that users cannot access other users' events! ✅

---

## 5. Error Cases

### 5.1 Missing Authentication Token

**Endpoint**: `GET /api/events`

**Headers**: (none)

**Expected Response** (401 Unauthorized):
```json
{
  "status": 401,
  "message": "Unauthorized",
  "timestamp": "2025-10-11T10:50:00"
}
```

---

### 5.2 Invalid Token

**Endpoint**: `GET /api/events`

**Headers**:
```
Authorization: Bearer invalid_token_here
```

**Expected Response** (401 Unauthorized)

---

### 5.3 Event Not Found

**Endpoint**: `GET /api/events/{eventId}`

**Headers**:
```
Authorization: Bearer JWT_TOKEN_HERE
```

**Expected Response** (404 Not Found):
```json
{
  "status": 404,
  "message": "Event not found with id: ...",
  "timestamp": "2025-10-11T10:52:00"
}
```

---

### 5.4 Validation Error (Missing Required Fields)

**Endpoint**: `POST /api/events`

**Headers**:
```
Authorization: Bearer JWT_TOKEN_HERE
Content-Type: application/json
```

**Request Body**:
```json
{
  "description": "Event without title"
}
```

**Expected Response** (400 Bad Request):
```json
{
  "title": "Title is required",
  "eventDate": "Event date is required",
  "location": "Location is required"
}
```

---

### 5.5 Invalid File Format

**Endpoint**: `POST /api/events/{eventId}/participants/upload`

**Headers**:
```
Authorization: Bearer JWT_TOKEN_HERE
Content-Type: multipart/form-data
```

**Form Data**:
- Key: `file`
- Value: Select .txt or .pdf file

**Expected Response** (400 Bad Request):
```json
{
  "status": 400,
  "message": "Unsupported file format. Please upload CSV file.",
  "timestamp": "2025-10-11T10:55:00"
}
```

---

## 6. cURL Commands

### Sign Up
```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Alice Johnson",
    "email": "alice@example.com",
    "password": "password123"
  }'
```

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "alice@example.com",
    "password": "password123"
  }'
```

### Create Event
```bash
curl -X POST http://localhost:8080/api/events \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer JWT_TOKEN_HERE" \
  -d '{
    "title": "Tech Conference 2025",
    "description": "Annual technology conference",
    "eventDate": "2025-12-15T09:00:00",
    "location": "San Francisco, CA"
  }'
```

### Get All Events
```bash
curl -X GET "http://localhost:8080/api/events?page=0&size=10" \
  -H "Authorization: Bearer JWT_TOKEN_HERE"
```

### Get Event by ID
```bash
curl -X GET http://localhost:8080/api/events/{eventId} \
  -H "Authorization: Bearer JWT_TOKEN_HERE"
```

### Update Event
```bash
curl -X PUT http://localhost:8080/api/events/{eventId} \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer JWT_TOKEN_HERE" \
  -d '{
    "title": "Tech Conference 2025 - Updated",
    "description": "Updated description",
    "eventDate": "2025-12-16T09:00:00",
    "location": "San Francisco, CA"
  }'
```

### Delete Event
```bash
curl -X DELETE http://localhost:8080/api/events/{eventId} \
  -H "Authorization: Bearer JWT_TOKEN_HERE"
```

### Upload Participants
```bash
curl -X POST http://localhost:8080/api/events/{eventId}/participants/upload \
  -H "Authorization: Bearer JWT_TOKEN_HERE" \
  -F "file=@participants.csv"
```

### Get Participants
```bash
curl -X GET "http://localhost:8080/api/events/{eventId}/participants?page=0&size=20" \
  -H "Authorization: Bearer JWT_TOKEN_HERE"
```

### Update Participant Status
```bash
curl -X PATCH "http://localhost:8080/api/events/{eventId}/participants/{participantId}/status?status=ACCEPTED" \
  -H "Authorization: Bearer JWT_TOKEN_HERE"
```

---

## 7. Testing Tips

1. **Use Swagger UI**: Easiest way to test - http://localhost:8080/swagger-ui
2. **Save Tokens**: Keep your JWT tokens handy during testing
3. **Check H2 Console**: View database state at http://localhost:8080/h2-console
4. **Test Isolation**: Create multiple users to verify data isolation
5. **Pagination**: Try different page sizes and sort orders
6. **File Uploads**: Test both CSV and Excel formats

---

## 8. Sample CSV File

Create a file named `participants.csv`:

```csv
name,email,phone
John Doe,john.doe@example.com,+1234567890
Jane Smith,jane.smith@example.com,+0987654321
Bob Wilson,bob.wilson@example.com,+1122334455
Alice Brown,alice.brown@example.com,
Charlie Davis,charlie.davis@example.com,+1555666777
```

---

## 9. Expected Behavior Summary

✅ **Authentication**
- Users can sign up and login
- JWT tokens are returned upon successful authentication
- Tokens expire after 24 hours

✅ **Authorization**
- All event/participant endpoints require authentication
- Users can only access their own events
- Attempting to access other users' events returns 403 Forbidden

✅ **Events**
- CRUD operations work correctly
- Search functionality filters by keyword
- Pagination works with customizable page size and sorting
- Deleting an event cascades to delete all participants

✅ **Participants**
- Can be uploaded via CSV or Excel files
- Invitation status defaults to PENDING
- Status can be updated to ACCEPTED or DECLINED
- Participants are tied to specific events
- Pagination supported

✅ **Error Handling**
- Proper HTTP status codes returned
- Clear error messages
- Validation errors include field-specific messages

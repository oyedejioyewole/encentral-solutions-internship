# Eventify - Quick Start Guide

Get up and running with Eventify in 5 minutes!

## 🚀 Quick Setup

### Step 1: Run the Application
```bash
./mvnw spring-boot:run
```

Wait for the success message:
```
🎉 Eventify API is running!
📖 Swagger UI: http://localhost:8080/swagger-ui
📄 API Docs: http://localhost:8080/api-docs
🗄️  H2 Console: http://localhost:8080/h2-console
```

### Step 2: Open Swagger UI
Navigate to: **http://localhost:8080/swagger-ui**

---

## 🎯 5-Minute Tutorial

### 1️⃣ Create an Account (30 seconds)

Click on **Authentication** → **POST /api/auth/signup** → **Try it out**

```json
{
  "name": "Mary Anne",
  "email": "mary.anne@example.com",
  "password": "testpassword123"
}
```

Click **Execute**. You'll get a response with a **JWT token**. Copy it!

---

### 2️⃣ Authorize Swagger (10 seconds)

1. Click the **🔓 Authorize** button at the top
2. Enter: `Bearer JWT_TOKEN_HERE`
3. Click **Authorize** then **Close**

You're now authenticated! 🎉

---

### 3️⃣ Create Your First Event (1 minute)

Click on **Event Management** → **POST /api/events** → **Try it out**

```json
{
  "title": "My First Event",
  "description": "Testing Eventify",
  "eventDate": "2025-12-25T10:00:00",
  "location": "Online"
}
```

Click **Execute**. Your event is created! Note the **id** (probably 1).

---

### 4️⃣ Add Participants (2 minutes)

#### Option A: Via Swagger Upload

1. Create a file `participants.csv`:
```csv
name,email,phone
John Doe,john@example.com,+1234567890
Jane Smith,jane@example.com,+0987654321
```

2. Click **Participant Management** → **POST /api/events/{eventId}/participants/upload**
3. Enter your event ID (1)
4. Click **Choose File** and select your CSV
5. Click **Execute**

#### Option B: Skip to Next Step
You can also test without participants!

---

### 5️⃣ View Your Event (30 seconds)

Click **Event Management** → **GET /api/events** → **Try it out** → **Execute**

You'll see:
- Your event details
- Participant count
- Pagination information

---

### 6️⃣ Update Participant Status (1 minute)

Click **Participant Management** → **PATCH /api/events/{eventId}/participants/{participantId}/status**

- Enter your event ID: `1`
- Enter participant ID: `1`
- Select status: `ACCEPTED`
- Click **Execute**

The participant status is now updated! ✅

---

## 🧪 Test Multi-User Isolation

### Create Second User
1. In Swagger, scroll to **Authentication**
2. **POST /api/auth/signup** with different email
3. Copy the new token
4. Click **🔓 Authorize** and replace with new token

### Try to Access First User's Events
**GET /api/events** → You'll see **NO events** (or only user 2's events)

Try **GET /api/events/{userId}** → You'll get **403 Forbidden**! ✅

This proves users can only see their own data!

---

## 📊 Quick Database Check

1. Go to: **http://localhost:8080/h2-console**
2. Use these credentials:
   - JDBC URL: `jdbc:h2:mem:eventifydb`
   - Username: `sa`
   - Password: (leave empty)
3. Click **Connect**

Run this query:
```sql
SELECT * FROM EVENTS;
SELECT * FROM PARTICIPANTS;
SELECT * FROM USERS;
```

You'll see all your data!

---

## 🎓 Key Concepts

### Authentication
- Sign up once, get a JWT token
- Token is valid for 24 hours
- Add token to all requests: `Authorization: Bearer JWT_TOKEN`

### Authorization
- Each user has their own isolated data
- Users cannot see or modify other users' events
- Attempting unauthorized access returns 403 Forbidden

### Pagination
All list endpoints support:
- `?page=0` - Page number (0-indexed)
- `&size=10` - Items per page
- `&sort=eventDate,asc` - Sort field and direction

Example: `/api/events?page=0&size=5&sort=title,asc`

### Invitation Status
- **PENDING** - Default status when participant is added
- **ACCEPTED** - Participant confirmed attendance
- **DECLINED** - Participant declined invitation

---

## 🔧 Common Operations

### Search Events
```
GET /api/events/search?keyword=conference
```

### Get Specific Event
```
GET /api/events/{id}
```

### Update Event (Partial)
```
PATCH /api/events/{id}
Body: { "location": "New Location" }
```

### Delete Event
```
DELETE /api/events/{id}
```
(This also deletes all participants)

---

## ❌ Troubleshooting

### "401 Unauthorized"
- Your token expired or is invalid
- Solution: Login again to get a new token

### "403 Forbidden"
- You're trying to access someone else's event
- Solution: Make sure you're using the correct token

### "404 Not Found"
- Event or participant doesn't exist
- Or it belongs to another user
- Solution: Check the ID and your authentication

### File Upload Fails
- Check file format (must be .csv)
- Ensure file has headers: name, email, phone
- Check file size (max 10MB)

---

## 🎯 Next Steps

1. ✅ Create multiple events
2. ✅ Upload participants via Excel
3. ✅ Test pagination with different page sizes
4. ✅ Search events by keywords
5. ✅ Create multiple users and verify isolation
6. ✅ Update invitation statuses
7. ✅ Export data from H2 console

---

## 📚 Full Documentation

For complete API documentation, see:
- **README.md** - Full project documentation
- **API_TESTING_GUIDE.md** - Detailed testing examples
- **Swagger UI** - Interactive API documentation

---

## 💡 Pro Tips

1. **Save Your Tokens**: Keep tokens in a text file during testing
2. **Use Swagger**: It's the easiest way to test the API
3. **Check H2 Console**: See real-time database changes
4. **Test Pagination**: Try different page sizes (5, 10, 20, 50)
5. **Test Authorization**: Create multiple users to see isolation
6. **Export Data**: Use H2 console to export CSV of your data

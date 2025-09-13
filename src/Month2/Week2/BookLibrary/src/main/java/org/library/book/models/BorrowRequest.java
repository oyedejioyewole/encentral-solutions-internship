package org.library.book.models;

import java.time.LocalDateTime;

public class BorrowRequest {
    private final String requestId;
    private final User user;
    private final String bookTitle;
    private final LocalDateTime requestTime;

    public BorrowRequest(String requestId, User user, String bookTitle) {
        this.requestId = requestId;
        this.user = user;
        this.bookTitle = bookTitle;
        this.requestTime = LocalDateTime.now();
    }

    // Getters
    public String getRequestId() { return requestId; }
    public User getUser() { return user; }
    public String getBookTitle() { return bookTitle; }
    public LocalDateTime getRequestTime() { return requestTime; }

    @Override
    public String toString() {
        return String.format("BorrowRequest{id='%s', user='%s', book='%s', time=%s, priority=%d}",
                requestId, user.getName(), bookTitle,
                requestTime.toLocalDate(), user.getPriorityLevel());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        BorrowRequest that = (BorrowRequest) obj;
        return requestId.equals(that.requestId);
    }

    @Override
    public int hashCode() {
        return requestId.hashCode();
    }
}
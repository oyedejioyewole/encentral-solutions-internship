package org.library.book.services;

import org.library.book.models.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Service for managing priority queues for book borrowing
 * Enhanced with comprehensive logging
 */
public class PriorityQueueService {
    private static final Logger logger = LogManager.getLogger(PriorityQueueService.class);

    /**
     * Create a priority queue for borrow requests
     * Priority order: Teachers > Senior Students > Junior Students
     * Within same priority: First Come, First Served (by timestamp)
     */
    public PriorityQueue<BorrowRequest> createPriorityQueue() {
        logger.debug("Creating new priority queue for borrow requests");
        return new PriorityQueue<>(getBorrowRequestComparator());
    }

    /**
     * Get the comparator used for prioritizing borrow requests
     */
    public Comparator<BorrowRequest> getBorrowRequestComparator() {
        return (request1, request2) -> {
            User user1 = request1.getUser();
            User user2 = request2.getUser();

            // Compare by priority level first (lower number = higher priority)
            int priority1 = user1.getPriorityLevel();
            int priority2 = user2.getPriorityLevel();

            logger.trace("Comparing requests - User1: {} (Priority: {}), User2: {} (Priority: {})",
                    user1.getName(), priority1, user2.getName(), priority2);

            if (priority1 != priority2) {
                int result = Integer.compare(priority1, priority2);
                logger.trace("Priority comparison result: {}", result);
                return result;
            }

            // Same priority level - compare by timestamp (FIFO)
            int timeComparison = request1.getRequestTime().compareTo(request2.getRequestTime());
            logger.trace("FIFO comparison result: {}", timeComparison);
            return timeComparison;
        };
    }

    /**
     * Get priority description for a user
     */
    public String getPriorityDescription(User user) {
        String description = switch (user.getPriorityLevel()) {
            case 1 -> "Highest (Teacher)";
            case 2 -> "Medium (Senior Student)";
            case 3 -> "Lower (Junior Student)";
            default -> "Unknown";
        };

        logger.debug("Priority description for user {}: {}", user.getName(), description);
        return description;
    }
}
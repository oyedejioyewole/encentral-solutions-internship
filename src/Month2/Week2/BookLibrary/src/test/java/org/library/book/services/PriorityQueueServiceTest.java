package org.library.book.services;

import org.junit.jupiter.api.*;
import org.library.book.models.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

class PriorityQueueServiceTest {
    private PriorityQueueService priorityQueueService;
    private Teacher teacher;
    private Student seniorStudent;
    private Student juniorStudent;

    @BeforeEach
    void setUp() {
        priorityQueueService = new PriorityQueueService();
        teacher = new Teacher("T001", "Dr. Smith", "Computer Science");
        seniorStudent = new Student("S001", "Alice Johnson", "senior");
        juniorStudent = new Student("S002", "Bob Wilson", "senior");
    }

    @Test
    @DisplayName("Should create priority queue with correct ordering")
    void shouldCreatePriorityQueueWithCorrectOrdering() {
        // Given
        PriorityQueue<BorrowRequest> queue = priorityQueueService.createPriorityQueue();

        // Create requests with slight delay to ensure different timestamps
        BorrowRequest juniorRequest = new BorrowRequest("REQ1", juniorStudent, "Test Book");

        try { Thread.sleep(10); } catch (InterruptedException _) {}
        BorrowRequest seniorRequest = new BorrowRequest("REQ2", seniorStudent, "Test Book");

        try { Thread.sleep(10); } catch (InterruptedException _) {}
        BorrowRequest teacherRequest = new BorrowRequest("REQ3", teacher, "Test Book");

        // When - add in reverse priority order
        queue.offer(juniorRequest);
        queue.offer(seniorRequest);
        queue.offer(teacherRequest);

        // Then - should come out in priority order
        assertEquals(teacherRequest, queue.poll());
        assertEquals(seniorRequest, queue.poll());
        assertEquals(juniorRequest, queue.poll());
    }

    @Test
    @DisplayName("Should handle same priority users with FIFO")
    void shouldHandleSamePriorityUsersWithFIFO() {
        // Given
        PriorityQueue<BorrowRequest> queue = priorityQueueService.createPriorityQueue();
        Student firstSenior = new Student("S001", "Alice", "senior");
        Student secondSenior = new Student("S002", "Bob", "senior");

        BorrowRequest firstRequest = new BorrowRequest("REQ1", firstSenior, "Test Book");

        try { Thread.sleep(10); } catch (InterruptedException _) {}
        BorrowRequest secondRequest = new BorrowRequest("REQ2", secondSenior, "Test Book");

        // When
        queue.offer(secondRequest);
        queue.offer(firstRequest);

        // Then - first request should come first (FIFO within same priority)
        assertEquals(firstRequest, queue.poll());
        assertEquals(secondRequest, queue.poll());
    }

    @Test
    @DisplayName("Should return correct priority description for teacher")
    void shouldReturnCorrectPriorityDescriptionForTeacher() {
        // When
        String description = priorityQueueService.getPriorityDescription(teacher);

        // Then
        assertEquals("Highest (Teacher)", description);
    }

    @Test
    @DisplayName("Should return correct priority description for senior student")
    void shouldReturnCorrectPriorityDescriptionForSeniorStudent() {
        // When
        String description = priorityQueueService.getPriorityDescription(seniorStudent);

        // Then
        assertEquals("Medium (Senior Student)", description);
    }

    @Test
    @DisplayName("Should return correct priority description for junior student")
    void shouldReturnCorrectPriorityDescriptionForJuniorStudent() {
        // When
        String description = priorityQueueService.getPriorityDescription(juniorStudent);

        // Then
        assertEquals("Lower (Junior Student)", description);
    }
}
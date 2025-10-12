package com.api.eventify.repository;

import com.api.eventify.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, String> {
    Page<Event> findByUserId(String userId, Pageable pageable);

    @Query(
        "SELECT e FROM Event e WHERE e.user.id = :userId AND (" +
            "LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.location) LIKE LOWER(CONCAT('%', :keyword, '%')))"
    )
    Page<Event> searchEventsByUser(
        @Param("userId") String userId,
        @Param("keyword") String keyword,
        Pageable pageable
    );
}

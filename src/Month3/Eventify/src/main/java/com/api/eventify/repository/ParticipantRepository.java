package com.api.eventify.repository;

import com.api.eventify.model.Participant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipantRepository
    extends JpaRepository<Participant, String> {
    Page<Participant> findByEventId(String eventId, Pageable pageable);
}

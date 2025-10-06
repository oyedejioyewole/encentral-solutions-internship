package com.api.eventify.repository;

import com.api.eventify.model.Participant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipantRepository
    extends JpaRepository<Participant, Long> {
    List<Participant> findByEventId(Long eventId);
}

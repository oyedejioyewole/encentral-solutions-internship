package com.api.eventify.service;

import com.api.eventify.dto.EventDTO;
import com.api.eventify.exception.ResourceNotFoundException;
import com.api.eventify.exception.UnauthorizedException;
import com.api.eventify.model.Event;
import com.api.eventify.model.User;
import com.api.eventify.repository.EventRepository;
import com.api.eventify.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final CustomUserDetailsService userDetailsService;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
            .getAuthentication()
            .getName();
        return userDetailsService.loadUserEntityByEmail(email);
    }

    private void verifyEventOwnership(Event event, User user) {
        if (!event.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException(
                "You do not have permission to access this event"
            );
        }
    }

    @Transactional
    public EventDTO createEvent(EventDTO eventDTO) {
        User currentUser = getCurrentUser();

        Event event = new Event();
        event.setTitle(eventDTO.getTitle());
        event.setDescription(eventDTO.getDescription());
        event.setEventDate(eventDTO.getEventDate());
        event.setLocation(eventDTO.getLocation());
        event.setUser(currentUser);

        Event savedEvent = eventRepository.save(event);
        return convertToDTO(savedEvent);
    }

    @Transactional(readOnly = true)
    public Page<EventDTO> getAllEvents(Pageable pageable) {
        User currentUser = getCurrentUser();
        return eventRepository
            .findByUserId(currentUser.getId(), pageable)
            .map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public EventDTO getEventById(String id) {
        User currentUser = getCurrentUser();

        Event event = eventRepository
            .findById(id)
            .orElseThrow(() ->
                new ResourceNotFoundException("Event not found with id: " + id)
            );
        verifyEventOwnership(event, currentUser);

        return convertToDTO(event);
    }

    @Transactional
    public EventDTO updateEvent(String id, EventDTO eventDTO) {
        User currentUser = getCurrentUser();

        Event event = eventRepository
            .findById(id)
            .orElseThrow(() ->
                new ResourceNotFoundException("Event not found with id: " + id)
            );

        verifyEventOwnership(event, currentUser);

        event.setTitle(eventDTO.getTitle());
        event.setDescription(eventDTO.getDescription());
        event.setEventDate(eventDTO.getEventDate());
        event.setLocation(eventDTO.getLocation());

        Event updatedEvent = eventRepository.save(event);
        return convertToDTO(updatedEvent);
    }

    @Transactional
    public EventDTO patchEvent(String id, EventDTO eventDTO) {
        User currentUser = getCurrentUser();
        Event event = eventRepository
            .findById(id)
            .orElseThrow(() ->
                new ResourceNotFoundException("Event not found with id: " + id)
            );

        verifyEventOwnership(event, currentUser);

        if (eventDTO.getTitle() != null) {
            event.setTitle(eventDTO.getTitle());
        }
        if (eventDTO.getDescription() != null) {
            event.setDescription(eventDTO.getDescription());
        }
        if (eventDTO.getEventDate() != null) {
            event.setEventDate(eventDTO.getEventDate());
        }
        if (eventDTO.getLocation() != null) {
            event.setLocation(eventDTO.getLocation());
        }

        Event updatedEvent = eventRepository.save(event);
        return convertToDTO(updatedEvent);
    }

    @Transactional
    public void deleteEvent(String id) {
        User currentUser = getCurrentUser();
        Event event = eventRepository
            .findById(id)
            .orElseThrow(() ->
                new ResourceNotFoundException("Event not found with id: " + id)
            );

        verifyEventOwnership(event, currentUser);
        eventRepository.delete(event);
    }

    @Transactional(readOnly = true)
    public Page<EventDTO> searchEvents(String keyword, Pageable pageable) {
        User currentUser = getCurrentUser();
        return eventRepository
            .searchEventsByUser(currentUser.getId(), keyword, pageable)
            .map(this::convertToDTO);
    }

    private EventDTO convertToDTO(Event event) {
        EventDTO dto = new EventDTO();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setEventDate(event.getEventDate());
        dto.setLocation(event.getLocation());
        dto.setCreatedAt(event.getCreatedAt());
        dto.setUpdatedAt(event.getUpdatedAt());
        dto.setParticipantCount(event.getParticipants().size());
        return dto;
    }
}

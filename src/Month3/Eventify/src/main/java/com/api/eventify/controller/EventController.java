package com.api.eventify.controller;

import com.api.eventify.dto.EventDTO;
import com.api.eventify.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@Tag(name = "Event Management", description = "APIs for managing events")
public class EventController {

    private final EventService eventService;

    @PostMapping
    @Operation(
        summary = "Create a new event",
        description = "Creates a new event with the provided details"
    )
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "201",
                description = "Event created successfully"
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
        }
    )
    public ResponseEntity<EventDTO> createEvent(
        @Valid @RequestBody EventDTO eventDTO
    ) {
        EventDTO created = eventService.createEvent(eventDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @Operation(
        summary = "Get all events",
        description = "Retrieves a list of all events"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Events retrieved successfully"
    )
    public ResponseEntity<List<EventDTO>> getAllEvents() {
        List<EventDTO> events = eventService.getAllEvents();
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get event by ID",
        description = "Retrieves a specific event by its ID"
    )
    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200", description = "Event found"),
            @ApiResponse(responseCode = "404", description = "Event not found"),
        }
    )
    public ResponseEntity<EventDTO> getEventById(
        @Parameter(description = "Event ID") @PathVariable Long id
    ) {
        EventDTO event = eventService.getEventById(id);
        return ResponseEntity.ok(event);
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Update event (full update)",
        description = "Updates all fields of an existing event"
    )
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "Event updated successfully"
            ),
            @ApiResponse(responseCode = "404", description = "Event not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
        }
    )
    public ResponseEntity<EventDTO> updateEvent(
        @Parameter(description = "Event ID") @PathVariable Long id,
        @Valid @RequestBody EventDTO eventDTO
    ) {
        EventDTO updated = eventService.updateEvent(id, eventDTO);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}")
    @Operation(
        summary = "Partially update event",
        description = "Updates only the provided fields of an existing event"
    )
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "Event updated successfully"
            ),
            @ApiResponse(responseCode = "404", description = "Event not found"),
        }
    )
    public ResponseEntity<EventDTO> patchEvent(
        @Parameter(description = "Event ID") @PathVariable Long id,
        @RequestBody EventDTO eventDTO
    ) {
        EventDTO updated = eventService.patchEvent(id, eventDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete event",
        description = "Deletes an event and all its participants"
    )
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "204",
                description = "Event deleted successfully"
            ),
            @ApiResponse(responseCode = "404", description = "Event not found"),
        }
    )
    public ResponseEntity<Void> deleteEvent(
        @Parameter(description = "Event ID") @PathVariable Long id
    ) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(
        summary = "Search events",
        description = "Searches events by title, description, or location"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Search completed successfully"
    )
    public ResponseEntity<List<EventDTO>> searchEvents(
        @Parameter(description = "Search keyword") @RequestParam String keyword
    ) {
        List<EventDTO> events = eventService.searchEvents(keyword);
        return ResponseEntity.ok(events);
    }
}

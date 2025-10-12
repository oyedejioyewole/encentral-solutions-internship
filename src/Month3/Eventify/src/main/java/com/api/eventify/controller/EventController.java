package com.api.eventify.controller;

import com.api.eventify.dto.EventDTO;
import com.api.eventify.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@Tag(name = "Event Management", description = "APIs for managing events")
@SecurityRequirement(name = "Bearer Authentication")
public class EventController {

    private final EventService eventService;

    @PostMapping
    @Operation(
        summary = "Create a new event",
        description = "Creates a new event for the authenticated user"
    )
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "201",
                description = "Event created successfully"
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
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
        description = "Retrieves a paginated list of all events for the authenticated user"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Events retrieved successfully"
    )
    public ResponseEntity<Page<EventDTO>> getAllEvents(
        @PageableDefault(
            size = 10,
            sort = "eventDate",
            direction = Sort.Direction.ASC
        ) Pageable pageable
    ) {
        Page<EventDTO> events = eventService.getAllEvents(pageable);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get event by ID",
        description = "Retrieves a specific event by its ID (must be owned by authenticated user)"
    )
    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200", description = "Event found"),
            @ApiResponse(responseCode = "404", description = "Event not found"),
            @ApiResponse(
                responseCode = "403",
                description = "Forbidden - not event owner"
            ),
        }
    )
    public ResponseEntity<EventDTO> getEventById(
        @Parameter(description = "Event ID") @PathVariable String id
    ) {
        EventDTO event = eventService.getEventById(id);
        return ResponseEntity.ok(event);
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Update event (full update)",
        description = "Updates all fields of an existing event (must be owned by authenticated user)"
    )
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "Event updated successfully"
            ),
            @ApiResponse(responseCode = "404", description = "Event not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(
                responseCode = "403",
                description = "Forbidden - not event owner"
            ),
        }
    )
    public ResponseEntity<EventDTO> updateEvent(
        @Parameter(description = "Event ID") @PathVariable String id,
        @Valid @RequestBody EventDTO eventDTO
    ) {
        EventDTO updated = eventService.updateEvent(id, eventDTO);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}")
    @Operation(
        summary = "Partially update event",
        description = "Updates only the provided fields of an existing event (must be owned by authenticated user)"
    )
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "Event updated successfully"
            ),
            @ApiResponse(responseCode = "404", description = "Event not found"),
            @ApiResponse(
                responseCode = "403",
                description = "Forbidden - not event owner"
            ),
        }
    )
    public ResponseEntity<EventDTO> patchEvent(
        @Parameter(description = "Event ID") @PathVariable String id,
        @RequestBody EventDTO eventDTO
    ) {
        EventDTO updated = eventService.patchEvent(id, eventDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete event",
        description = "Deletes an event and all its participants (must be owned by authenticated user)"
    )
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "204",
                description = "Event deleted successfully"
            ),
            @ApiResponse(responseCode = "404", description = "Event not found"),
            @ApiResponse(
                responseCode = "403",
                description = "Forbidden - not event owner"
            ),
        }
    )
    public ResponseEntity<Void> deleteEvent(
        @Parameter(description = "Event ID") @PathVariable String id
    ) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(
        summary = "Search events",
        description = "Searches events by title, description, or location (only user's own events)"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Search completed successfully"
    )
    public ResponseEntity<Page<EventDTO>> searchEvents(
        @Parameter(description = "Search keyword") @RequestParam String keyword,
        @PageableDefault(
            size = 10,
            sort = "eventDate",
            direction = Sort.Direction.ASC
        ) Pageable pageable
    ) {
        Page<EventDTO> events = eventService.searchEvents(keyword, pageable);
        return ResponseEntity.ok(events);
    }
}

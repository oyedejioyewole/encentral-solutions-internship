package com.api.eventify.controller;

import com.api.eventify.dto.ParticipantDTO;
import com.api.eventify.model.Participant;
import com.api.eventify.service.ParticipantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/events/{eventId}/participants")
@RequiredArgsConstructor
@Tag(
    name = "Participant Management",
    description = "APIs for managing event participants"
)
@SecurityRequirement(name = "Bearer Authentication")
public class ParticipantController {

    private final ParticipantService participantService;

    @GetMapping
    @Operation(
        summary = "Get all participants",
        description = "Retrieves paginated participants for a specific event (must be event owner)"
    )
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "Participants retrieved successfully"
            ),
            @ApiResponse(responseCode = "404", description = "Event not found"),
            @ApiResponse(
                responseCode = "403",
                description = "Forbidden - not event owner"
            ),
        }
    )
    public ResponseEntity<Page<ParticipantDTO>> getParticipantsByEvent(
        @Parameter(description = "Event ID") @PathVariable String eventId,
        @PageableDefault(
            size = 20,
            sort = "createdAt",
            direction = Sort.Direction.DESC
        ) Pageable pageable
    ) {
        Page<ParticipantDTO> participants =
            participantService.getParticipantsByEventId(eventId, pageable);
        return ResponseEntity.ok(participants);
    }

    @PostMapping(
        value = "/upload",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @Operation(
        summary = "Upload participants",
        description = "Upload participants from CSV or Excel file. File should contain columns: name, email, phone (optional)"
    )
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "201",
                description = "Participants uploaded successfully"
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Invalid file format or content"
            ),
            @ApiResponse(responseCode = "404", description = "Event not found"),
            @ApiResponse(
                responseCode = "403",
                description = "Forbidden - not event owner"
            ),
        }
    )
    public ResponseEntity<List<ParticipantDTO>> uploadParticipants(
        @Parameter(description = "Event ID") @PathVariable String eventId,
        @Parameter(
            description = "CSV or Excel file containing participant data",
            content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
        ) @RequestParam("file") MultipartFile file
    ) {
        List<ParticipantDTO> participants =
            participantService.uploadParticipants(eventId, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(participants);
    }

    @PatchMapping("/{participantId}/status")
    @Operation(
        summary = "Update participant invitation status",
        description = "Updates the invitation status of a participant (ACCEPTED or DECLINED)"
    )
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "Status updated successfully"
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Event or Participant not found"
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Invalid status value"
            ),
            @ApiResponse(
                responseCode = "403",
                description = "Forbidden - not event owner"
            ),
        }
    )
    public ResponseEntity<ParticipantDTO> updateParticipantStatus(
        @Parameter(description = "Event ID") @PathVariable String eventId,
        @Parameter(
            description = "Participant ID"
        ) @PathVariable String participantId,
        @Parameter(
            description = "Invitation status (PENDING, ACCEPTED, or DECLINED)"
        ) @RequestParam Participant.InvitationStatus status
    ) {
        ParticipantDTO updated = participantService.updateParticipantStatus(
            eventId,
            participantId,
            status
        );
        return ResponseEntity.ok(updated);
    }
}

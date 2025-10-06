package com.api.eventify.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {

    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Event date is required")
    private LocalDateTime eventDate;

    @NotBlank(message = "Location is required")
    private String location;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer participantCount;
}

// Request DTOs
@Data
@NoArgsConstructor
@AllArgsConstructor
class EventCreateRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Event date is required")
    private LocalDateTime eventDate;

    @NotBlank(message = "Location is required")
    private String location;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class EventUpdateRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Event date is required")
    private LocalDateTime eventDate;

    @NotBlank(message = "Location is required")
    private String location;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class EventPatchRequest {

    private String title;
    private String description;
    private LocalDateTime eventDate;
    private String location;
}

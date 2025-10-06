package com.api.eventify.service;

import com.api.eventify.dto.ParticipantDTO;
import com.api.eventify.exception.InvalidFileException;
import com.api.eventify.exception.ResourceNotFoundException;
import com.api.eventify.model.Event;
import com.api.eventify.model.Participant;
import com.api.eventify.repository.EventRepository;
import com.api.eventify.repository.ParticipantRepository;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ParticipantService {

    private final ParticipantRepository participantRepository;
    private final EventRepository eventRepository;

    @Transactional(readOnly = true)
    public List<ParticipantDTO> getParticipantsByEventId(Long eventId) {
        // Verify event exists
        eventRepository
            .findById(eventId)
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    "Event not found with id: " + eventId
                )
            );

        return participantRepository
            .findByEventId(eventId)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Transactional
    public List<ParticipantDTO> uploadParticipants(
        Long eventId,
        MultipartFile file
    ) {
        Event event = eventRepository
            .findById(eventId)
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    "Event not found with id: " + eventId
                )
            );

        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new InvalidFileException("Filename is required");
        }

        List<Participant> participants;

        if (filename.endsWith(".csv")) {
            participants = parseCSV(file, event);
        } else if (filename.endsWith(".xlsx") || filename.endsWith(".xls")) {
            participants = parseExcel(file, event);
        } else {
            throw new InvalidFileException(
                "Unsupported file format. Please upload CSV or Excel file."
            );
        }

        List<Participant> savedParticipants = participantRepository.saveAll(
            participants
        );
        return savedParticipants
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    private List<Participant> parseCSV(MultipartFile file, Event event) {
        List<Participant> participants = new ArrayList<>();

        try (
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream())
            )
        ) {
            String line;
            boolean isHeader = true;

            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue; // Skip header row
                }

                String[] fields = line.split(",");
                if (fields.length >= 2) {
                    // At least name and email
                    Participant participant = new Participant();
                    participant.setName(fields[0].trim());
                    participant.setEmail(fields[1].trim());

                    if (fields.length > 2 && !fields[2].trim().isEmpty()) {
                        participant.setPhone(fields[2].trim());
                    }

                    participant.setEvent(event);
                    participant.setInvitationStatus(
                        Participant.InvitationStatus.PENDING
                    );
                    participants.add(participant);
                }
            }
        } catch (Exception e) {
            throw new InvalidFileException(
                "Error parsing CSV file: " + e.getMessage()
            );
        }

        if (participants.isEmpty()) {
            throw new InvalidFileException(
                "No valid participant data found in file"
            );
        }

        return participants;
    }

    private List<Participant> parseExcel(MultipartFile file, Event event) {
        List<Participant> participants = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            boolean isHeader = true;

            for (Row row : sheet) {
                if (isHeader) {
                    isHeader = false;
                    continue; // Skip header row
                }

                Cell nameCell = row.getCell(0);
                Cell emailCell = row.getCell(1);
                Cell phoneCell = row.getCell(2);

                if (nameCell != null && emailCell != null) {
                    Participant participant = new Participant();
                    participant.setName(getCellValueAsString(nameCell));
                    participant.setEmail(getCellValueAsString(emailCell));

                    if (phoneCell != null) {
                        participant.setPhone(getCellValueAsString(phoneCell));
                    }

                    participant.setEvent(event);
                    participant.setInvitationStatus(
                        Participant.InvitationStatus.PENDING
                    );
                    participants.add(participant);
                }
            }
        } catch (Exception e) {
            throw new InvalidFileException(
                "Error parsing Excel file: " + e.getMessage()
            );
        }

        if (participants.isEmpty()) {
            throw new InvalidFileException(
                "No valid participant data found in file"
            );
        }

        return participants;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                return String.valueOf((long) cell.getNumericCellValue());
            default:
                return "";
        }
    }

    private ParticipantDTO convertToDTO(Participant participant) {
        ParticipantDTO dto = new ParticipantDTO();
        dto.setId(participant.getId());
        dto.setName(participant.getName());
        dto.setEmail(participant.getEmail());
        dto.setPhone(participant.getPhone());
        dto.setInvitationStatus(participant.getInvitationStatus());
        dto.setEventId(participant.getEvent().getId());
        dto.setCreatedAt(participant.getCreatedAt());
        return dto;
    }
}

package com.example.unitedservice.controllers;

import com.example.unitedservice.dto.MissionRequestDTO;
import com.example.unitedservice.entities.Status;
import com.example.unitedservice.entities.Worker;
import com.example.unitedservice.services.MissionRequestService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/missions")
@RequiredArgsConstructor
public class MissionRequestController {
    private final MissionRequestService missionService;
    private static final Logger logger = LoggerFactory.getLogger(MissionRequestController.class);

    @GetMapping
    public List<MissionRequestDTO> allMissions() {
        return missionService.getAllMissionRequests();
    }

    @GetMapping("/status/{status}")
    public List<MissionRequestDTO> byStatus(@PathVariable Status status) {
        return missionService.getByStatus(status);
    }

    @PostMapping
    public ResponseEntity<MissionRequestDTO> create(
            @RequestParam Long workerId,
            @RequestParam String destination,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate missionDate
    ) {
        try {
            MissionRequestDTO savedMission = missionService.submitMissionRequest(workerId, destination, missionDate);
            return ResponseEntity.ok(savedMission);
        } catch (RuntimeException e) {
            // Log the full exception
            logger.error("Error creating mission request for workerId: {}", workerId, e);
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<MissionRequestDTO> getById(@PathVariable Long id) {
        return missionService.getMissionRequestById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        missionService.deleteMissionRequest(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/rh-decision/{id}")
    public ResponseEntity<MissionRequestDTO> updateRHDecision(
            @PathVariable Long id,
            @RequestParam boolean approved,
            @RequestParam(required = false) String comment) {
        try {
            MissionRequestDTO updatedMission = missionService.updateRHStatus(id, approved, comment != null ? comment : "");
            return ResponseEntity.ok(updatedMission);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/admin-decision/{id}")
    public ResponseEntity<MissionRequestDTO> finalApprove(
            @PathVariable Long id,
            @RequestParam boolean approved,
            @RequestParam(required = false) String comment) {
        try {
            MissionRequestDTO updatedMission = missionService.finalApprove(id, approved, comment != null ? comment : "");
            return ResponseEntity.ok(updatedMission);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/has-pending/{workerId}")
    public boolean hasPending(@PathVariable Long workerId) {
        return missionService.hasPendingRequest(workerId);
    }
}
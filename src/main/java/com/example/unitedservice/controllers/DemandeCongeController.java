package com.example.unitedservice.controllers;

import com.example.unitedservice.dto.DemandeCongeDTO;
import com.example.unitedservice.entities.Status;
import com.example.unitedservice.entities.TypeConge;
import com.example.unitedservice.services.DemandeCongeService;
import com.example.unitedservice.services.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/conges")
@RequiredArgsConstructor
class DemandeCongeController {
    private final DemandeCongeService congeService;
    private final ModelMapper modelMapper;
    private final FileStorageService fileStorageService;
    @GetMapping
    public List<DemandeCongeDTO> allConges() {
        return congeService.getAllDemandes();
    }

    @GetMapping("/status/{status}")
    public List<DemandeCongeDTO> byStatus(@PathVariable Status status) {
        return congeService.getByStatus(status);
    }

    @PostMapping
    public ResponseEntity<DemandeCongeDTO> create(
            @RequestParam Long workerId,
            @RequestParam TypeConge type,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam String reason,
            @RequestParam(required = false) MultipartFile attachment) {
        try {
            DemandeCongeDTO savedDemande = congeService.submitDemande(workerId, type, startDate, endDate, reason, attachment);
            return ResponseEntity.ok(savedDemande);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null); // Handle worker not found or file storage errors
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<DemandeCongeDTO> update(@PathVariable Long id, @RequestBody DemandeCongeDTO congeDTO) {
        return congeService.getDemandeById(id)
                .map(dto -> {
                    DemandeCongeDTO updatedConge = congeService.saveDemande(congeDTO);
                    return ResponseEntity.ok(updatedConge);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        congeService.deleteDemande(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/recent/{monthsBack}")
    public List<DemandeCongeDTO> recent(@PathVariable int monthsBack) {
        return congeService.findRecentCongeForDashboard(monthsBack);
    }

    @GetMapping("/count/all")
    public int countAll() {
        return congeService.countAll();
    }

    @GetMapping("/count/status/{status}")
    public int countByStatus(@PathVariable Status status) {
        return congeService.countByStatus(status);
    }

    @GetMapping("/count/type/{type}")
    public int countByType(@PathVariable TypeConge type) {
        return congeService.countByType(type);
    }

    @GetMapping("/count-by-month/{month}/{year}")
    public int countByMonth(@PathVariable int month, @PathVariable int year) {
        return congeService.countByMonth(month, year);
    }

    @GetMapping("/has-pending/{workerId}")
    public boolean hasPending(@PathVariable Long workerId) {
        return congeService.hasPendingRequest(workerId);
    }

    @PutMapping("/rh-decision/{id}")
    public ResponseEntity<DemandeCongeDTO> updateRHDecision(@PathVariable Long id, @RequestParam boolean approved) {
        try {
            DemandeCongeDTO updatedConge = congeService.updateRHStatus(id, approved);
            return ResponseEntity.ok(updatedConge);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/admin-decision/{id}")
    public ResponseEntity<DemandeCongeDTO> finalApprove(@PathVariable Long id, @RequestParam boolean approved) {
        try {
            DemandeCongeDTO updatedConge = congeService.finalApprove(id, approved);
            return ResponseEntity.ok(updatedConge);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/download/{filename:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        Resource resource = fileStorageService.loadFile(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
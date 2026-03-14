package com.example.unitedservice.controllers;
import com.example.unitedservice.dto.DemandeAvanceDTO;
import com.example.unitedservice.entities.Status;
import com.example.unitedservice.services.DemandeAvanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/avances")
@RequiredArgsConstructor
public class DemandeAvanceController {
    private final DemandeAvanceService demandeAvanceService;

    @GetMapping
    public List<DemandeAvanceDTO> allDemandes() {
        return demandeAvanceService.getAllDemandeAvances();
    }

    @GetMapping("/status/{status}")
    public List<DemandeAvanceDTO> byStatus(@PathVariable Status status) {
        return demandeAvanceService.getByStatus(status);
    }

    @PostMapping
    public ResponseEntity<DemandeAvanceDTO> create(
            @RequestParam Long workerId,
            @RequestParam double requestedAmount) {
        try {
            DemandeAvanceDTO savedDemande = demandeAvanceService.submitDemandeAvance(workerId, requestedAmount);
            return ResponseEntity.ok(savedDemande);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<DemandeAvanceDTO> getById(@PathVariable Long id) {
        return demandeAvanceService.getDemandeAvanceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        demandeAvanceService.deleteDemandeAvance(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/admin-response/{id}")
    public ResponseEntity<DemandeAvanceDTO> updateAdminResponse(
            @PathVariable Long id,
            @RequestParam double adminResponseAmount,
            @RequestParam(required = false) String adminComment) {
        try {
            DemandeAvanceDTO updatedDemande = demandeAvanceService.updateAdminResponse(id, adminResponseAmount, adminComment != null ? adminComment : "");
            return ResponseEntity.ok(updatedDemande);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/has-pending/{workerId}")
    public boolean hasPending(@PathVariable Long workerId) {
        return demandeAvanceService.hasPendingRequest(workerId);
    }
}
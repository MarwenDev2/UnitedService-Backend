package com.example.unitedservice.services;

import com.example.unitedservice.dto.DemandeCongeDTO;
import com.example.unitedservice.dto.NotificationRequest;
import com.example.unitedservice.dto.WorkerDTO;
import com.example.unitedservice.entities.DemandeConge;
import com.example.unitedservice.entities.Status;
import com.example.unitedservice.entities.TypeConge;
import com.example.unitedservice.entities.Worker;
import com.example.unitedservice.repositories.DemandeCongeRepository;
import com.example.unitedservice.repositories.UserRepository;
import com.example.unitedservice.repositories.WorkerRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DemandeCongeService {
    private final DemandeCongeRepository demandeCongeRepository;
    private final WorkerRepository workerRepository;
    private final ModelMapper modelMapper;
    private final FileStorageService fileStorageService;
    private final PushNotificationService pushNotificationService;
    private final UserRepository userRepository;

    public List<DemandeCongeDTO> getAllDemandes() {
        return demandeCongeRepository.findAll().stream()
                .map(conge -> modelMapper.map(conge, DemandeCongeDTO.class))
                .collect(Collectors.toList());
    }

    public List<DemandeCongeDTO> getByWorker(Long workerId) {
        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new RuntimeException("Worker with ID " + workerId + " not found"));
        return demandeCongeRepository.findByWorker(worker).stream()
                .map(conge -> modelMapper.map(conge, DemandeCongeDTO.class))
                .collect(Collectors.toList());
    }

    public List<DemandeCongeDTO> getByStatus(Status status) {
        return demandeCongeRepository.findByStatus(status).stream()
                .map(conge -> modelMapper.map(conge, DemandeCongeDTO.class))
                .collect(Collectors.toList());
    }

    public DemandeCongeDTO saveDemande(DemandeCongeDTO demandeDTO) {
        DemandeConge demande = modelMapper.map(demandeDTO, DemandeConge.class);
        DemandeConge savedDemande = demandeCongeRepository.save(demande);
        return modelMapper.map(savedDemande, DemandeCongeDTO.class);
    }

    public Optional<DemandeCongeDTO> getDemandeById(Long id) {
        return demandeCongeRepository.findById(id)
                .map(conge -> modelMapper.map(conge, DemandeCongeDTO.class));
    }

    public void deleteDemande(Long id) {
        demandeCongeRepository.deleteById(id);
    }

    public boolean hasPendingRequest(Long workerId) {
        return demandeCongeRepository.countByWorkerIdAndStatusIn(
                workerId, List.of(Status.EN_ATTENTE_RH, Status.EN_ATTENTE_ADMIN)) > 0;
    }

    public int countByStatus(Status status) {
        return demandeCongeRepository.countByStatus(status);
    }

    public int countByType(TypeConge type) {
        return demandeCongeRepository.countByType(type);
    }

    public int countByMonth(int month, int year) {
        return demandeCongeRepository.countByMonth(month, year);
    }

    public int countAll() {
        return (int) demandeCongeRepository.count();
    }

    public List<DemandeCongeDTO> findRecentCongeForDashboard(int monthsBack) {
        LocalDate since = LocalDate.now().minusMonths(monthsBack);
        return demandeCongeRepository.findRecentConge(since).stream()
                .map(conge -> modelMapper.map(conge, DemandeCongeDTO.class))
                .collect(Collectors.toList());
    }

    public DemandeCongeDTO updateRHStatus(Long demandeId, boolean isApproved) {
        Optional<DemandeConge> optionalDemande = demandeCongeRepository.findById(demandeId);
        if (optionalDemande.isPresent()) {
            DemandeConge demande = optionalDemande.get();
            demande.setStatus(isApproved ? Status.EN_ATTENTE_ADMIN : Status.REFUSE_RH);
            DemandeConge updatedDemande = demandeCongeRepository.save(demande);

            // Send push notification
            sendLeaveRequestNotification(demande, isApproved, "RH");

            return modelMapper.map(updatedDemande, DemandeCongeDTO.class);
        }
        throw new RuntimeException("DemandeConge with ID " + demandeId + " not found");
    }



    public DemandeCongeDTO finalApprove(Long demandeId, boolean isApproved) {
        Optional<DemandeConge> optionalDemande = demandeCongeRepository.findById(demandeId);
        if (optionalDemande.isPresent()) {
            DemandeConge demande = optionalDemande.get();
            demande.setStatus(isApproved ? Status.ACCEPTE : Status.REFUSE_ADMIN);
            if (isApproved) {
                // Calculate the number of days
                long days = ChronoUnit.DAYS.between(demande.getStartDate(), demande.getEndDate()) + 1;

                Worker worker = demande.getWorker();
                // ✅ Update worker's used and total leave days
                worker.setUsedCongeDays(worker.getUsedCongeDays() + (int) days);

                // ✅ Decrease total credit
                worker.setTotalCongeDays(worker.getTotalCongeDays() - (int) days);

                // Update last credit update date
                worker.setLastCongeCreditUpdate(LocalDate.now());
                workerRepository.save(worker);
            }
            DemandeConge updatedDemande = demandeCongeRepository.save(demande);

            // Send push notification
            sendLeaveRequestNotification(demande, isApproved, "ADMIN");


            return modelMapper.map(updatedDemande, DemandeCongeDTO.class);
        }
        throw new RuntimeException("DemandeConge with ID " + demandeId + " not found");
    }

    private void sendLeaveRequestNotification(DemandeConge demande, boolean isApproved, String approvedBy) {
        try {
            NotificationRequest notification = new NotificationRequest();

            // Set notification title and body
            String statusText = isApproved ? "Approved" : "Rejected";
            notification.setTitle("Leave Request " + statusText);
            notification.setBody("Leave request for " + demande.getWorker().getName() +
                    " has been " + statusText.toLowerCase() + " by " + approvedBy);
            notification.setIcon("/uploads/photos/logo.ico");
            notification.setType("leave_request");

            // Create data payload
            Map<String, Object> data = new HashMap<>();
            data.put("url", "/conges");
            data.put("type", "leave_request");
            data.put("status", isApproved ? "approved" : "rejected");
            data.put("requestId", demande.getId());
            data.put("employeeName", demande.getWorker().getName());
            data.put("approvedBy", approvedBy);
            data.put("timestamp", LocalDateTime.now().toString());
            notification.setData(data);

            // Send to ALL users (or specific users if needed)
            pushNotificationService.sendNotificationToAll(notification);

            System.out.println("🚀 PUSH NOTIFICATION SENT for leave request ID: " + demande.getId());
            System.out.println("📝 Title: " + notification.getTitle());
            System.out.println("📄 Body: " + notification.getBody());

        } catch (Exception e) {
            System.err.println("❌ Failed to send push notification for leave request: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public DemandeCongeDTO submitDemande(Long workerId, TypeConge type, LocalDate startDate, LocalDate endDate, String reason, MultipartFile attachment) {
        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new RuntimeException("Worker with ID " + workerId + " not found"));

        DemandeConge demande = new DemandeConge();
        demande.setWorker(worker);
        demande.setType(type);
        demande.setStartDate(startDate);
        demande.setEndDate(endDate);
        demande.setReason(reason);
        demande.setDateDemande(LocalDate.now());
        demande.setStatus(Status.EN_ATTENTE_RH);

        if (attachment != null) {
            String fileName = fileStorageService.storeFile(attachment);
            demande.setAttachmentPath(fileName);
        }

        DemandeConge savedDemande = demandeCongeRepository.save(demande);
        return modelMapper.map(savedDemande, DemandeCongeDTO.class);
    }

    public String checkEligibility(Long workerId, LocalDate startDate, LocalDate endDate) {
        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new RuntimeException("Worker not found"));

        // Check pending request
        if (hasPendingRequest(workerId)) {
            return "Vous avez déjà une demande de congé en attente.";
        }

        // Check overlapping approved congé
        boolean hasConflict = demandeCongeRepository.existsApprovedCongeInPeriod(workerId, startDate, endDate);
        if (hasConflict) {
            return "Conflit détecté : une période de congé approuvée existe déjà.";
        }

        // Check credit
        long requestedDays = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
        int remainingDays = worker.getTotalCongeDays() - worker.getUsedCongeDays();
        if (requestedDays > remainingDays) {
            return "Solde insuffisant. Jours restants: " + remainingDays;
        }

        return "OK"; // Eligible
    }

}
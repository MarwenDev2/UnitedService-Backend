package com.example.unitedservice.services;

import com.example.unitedservice.dto.DemandeCongeDTO;
import com.example.unitedservice.dto.WorkerDTO;
import com.example.unitedservice.entities.DemandeConge;
import com.example.unitedservice.entities.Status;
import com.example.unitedservice.entities.TypeConge;
import com.example.unitedservice.entities.Worker;
import com.example.unitedservice.repositories.DemandeCongeRepository;
import com.example.unitedservice.repositories.WorkerRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DemandeCongeService {
    private final DemandeCongeRepository demandeCongeRepository;
    private final WorkerRepository workerRepository;
    private final ModelMapper modelMapper;
    private final FileStorageService fileStorageService;

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

    public int countByMonth(int month) {
        return demandeCongeRepository.countByMonth(month);
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
            return modelMapper.map(updatedDemande, DemandeCongeDTO.class);
        }
        throw new RuntimeException("DemandeConge with ID " + demandeId + " not found");
    }

    public DemandeCongeDTO finalApprove(Long demandeId, boolean isApproved) {
        Optional<DemandeConge> optionalDemande = demandeCongeRepository.findById(demandeId);
        if (optionalDemande.isPresent()) {
            DemandeConge demande = optionalDemande.get();
            demande.setStatus(isApproved ? Status.ACCEPTE : Status.REFUSE_ADMIN);
            DemandeConge updatedDemande = demandeCongeRepository.save(demande);
            return modelMapper.map(updatedDemande, DemandeCongeDTO.class);
        }
        throw new RuntimeException("DemandeConge with ID " + demandeId + " not found");
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
}
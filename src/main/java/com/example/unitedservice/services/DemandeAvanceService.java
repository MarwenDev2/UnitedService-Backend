package com.example.unitedservice.services;
import com.example.unitedservice.dto.DemandeAvanceDTO;
import com.example.unitedservice.entities.DemandeAvance;
import com.example.unitedservice.entities.Status;
import com.example.unitedservice.entities.Worker;
import com.example.unitedservice.repositories.DemandeAvanceRepository;
import com.example.unitedservice.repositories.WorkerRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class DemandeAvanceService {

    private final DemandeAvanceRepository demandeAvanceRepository;
    private final WorkerRepository workerRepository;
    private final ModelMapper modelMapper;

    public List<DemandeAvanceDTO> getAllDemandeAvances() {
        return demandeAvanceRepository.findAll().stream()
                .map(avance -> modelMapper.map(avance, DemandeAvanceDTO.class))
                .collect(Collectors.toList());
    }

    public List<DemandeAvanceDTO> getByStatus(Status status) {
        return demandeAvanceRepository.findByStatus(status).stream()
                .map(avance -> modelMapper.map(avance, DemandeAvanceDTO.class))
                .collect(Collectors.toList());
    }
    public DemandeAvanceDTO submitDemandeAvance(Long workerId, double requestedAmount) {
        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new RuntimeException("Worker with ID " + workerId + " not found"));
        DemandeAvance demande = new DemandeAvance();
        demande.setWorker(worker);
        demande.setRequestedAmount(requestedAmount);
        demande.setDateRequest(LocalDate.now());
        demande.setStatus(Status.EN_ATTENTE_ADMIN); // Directly to admin
        DemandeAvance savedDemande = demandeAvanceRepository.save(demande);
        return modelMapper.map(savedDemande, DemandeAvanceDTO.class);
    }
    public Optional<DemandeAvanceDTO> getDemandeAvanceById(Long id) {
        return demandeAvanceRepository.findById(id)
                .map(avance -> modelMapper.map(avance, DemandeAvanceDTO.class));
    }

    public void deleteDemandeAvance(Long id) {
        demandeAvanceRepository.deleteById(id);
    }
    public DemandeAvanceDTO updateAdminResponse(Long demandeId, double adminResponseAmount, String adminComment) {
        Optional<DemandeAvance> optionalDemande = demandeAvanceRepository.findById(demandeId);
        if (optionalDemande.isPresent()) {
            DemandeAvance demande = optionalDemande.get();
            if (demande.getStatus() != Status.EN_ATTENTE_ADMIN) {
                throw new RuntimeException("Invalid status for admin response");
            }
            demande.setAdminResponseAmount(adminResponseAmount);
            demande.setAdminComment(adminComment);
            demande.setStatus(adminResponseAmount > 0 ? Status.ACCEPTE : Status.REFUSE_ADMIN);
            DemandeAvance updatedDemande = demandeAvanceRepository.save(demande);
            return modelMapper.map(updatedDemande, DemandeAvanceDTO.class);
        }
        throw new RuntimeException("DemandeAvanceSalaire with ID " + demandeId + " not found");
    }

    public boolean hasPendingRequest(Long workerId) {
        return demandeAvanceRepository.countByWorkerIdAndStatusIn(
                workerId, List.of(Status.EN_ATTENTE_ADMIN)) > 0;
    }
}
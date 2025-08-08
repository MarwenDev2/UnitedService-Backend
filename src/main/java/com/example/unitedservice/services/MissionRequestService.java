package com.example.unitedservice.services;

import com.example.unitedservice.dto.MissionRequestDTO;
import com.example.unitedservice.entities.Decision;
import com.example.unitedservice.entities.MissionRequest;
import com.example.unitedservice.entities.Status;
import com.example.unitedservice.entities.Worker;
import com.example.unitedservice.repositories.MissionRequestRepository;
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
public class MissionRequestService {
    private final MissionRequestRepository missionRequestRepository;
    private final WorkerRepository workerRepository;
    private final ModelMapper modelMapper;
    private final FileStorageService fileStorageService;

    public List<MissionRequestDTO> getAllMissionRequests() {
        return missionRequestRepository.findAll().stream()
                .map(mission -> modelMapper.map(mission, MissionRequestDTO.class))
                .collect(Collectors.toList());
    }

    public List<MissionRequestDTO> getByStatus(Status status) {
        return missionRequestRepository.findByStatus(status).stream()
                .map(mission -> modelMapper.map(mission, MissionRequestDTO.class))
                .collect(Collectors.toList());
    }

    public MissionRequestDTO submitMissionRequest(Long workerId, String destination, LocalDate missionDate) {
        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new RuntimeException("Worker not found with id: " + workerId));

        MissionRequest mission = new MissionRequest();
        mission.setWorker(worker);
        mission.setDestination(destination);
        mission.setMissionDate(missionDate);
        mission.setStatus(Status.EN_ATTENTE_RH);

        // Initialize secretaire decision (approved by default on submission)
        Decision secretaireDecision = new Decision();
        secretaireDecision.setApproved(true); // Secretary submits, so approved by default
        mission.setSecretaireDecision(secretaireDecision);

        MissionRequest savedMission = missionRequestRepository.save(mission);
        return modelMapper.map(savedMission, MissionRequestDTO.class);
    }

    public Optional<MissionRequestDTO> getMissionRequestById(Long id) {
        return missionRequestRepository.findById(id)
                .map(mission -> modelMapper.map(mission, MissionRequestDTO.class));
    }

    public void deleteMissionRequest(Long id) {
        missionRequestRepository.deleteById(id);
    }

    public MissionRequestDTO updateRHStatus(Long missionId, boolean isApproved, String comment) {
        Optional<MissionRequest> optionalMission = missionRequestRepository.findById(missionId);
        if (optionalMission.isPresent()) {
            MissionRequest mission = optionalMission.get();
            if (mission.getStatus() == Status.EN_ATTENTE_RH) {
                Decision rhDecision = new Decision();
                rhDecision.setApproved(isApproved);
                rhDecision.setComment(comment);
                mission.setRhDecision(rhDecision);
                mission.setStatus(isApproved ? Status.EN_ATTENTE_ADMIN : Status.REFUSE_RH);
                MissionRequest updatedMission = missionRequestRepository.save(mission);
                return modelMapper.map(updatedMission, MissionRequestDTO.class);
            }
            throw new RuntimeException("Invalid status for RH decision");
        }
        throw new RuntimeException("MissionRequest with ID " + missionId + " not found");
    }

    public MissionRequestDTO finalApprove(Long missionId, boolean isApproved, String comment) {
        Optional<MissionRequest> optionalMission = missionRequestRepository.findById(missionId);
        if (optionalMission.isPresent()) {
            MissionRequest mission = optionalMission.get();
            if (mission.getStatus() == Status.EN_ATTENTE_ADMIN) {
                Decision adminDecision = new Decision();
                adminDecision.setApproved(isApproved);
                adminDecision.setComment(comment);
                mission.setAdminDecision(adminDecision);
                mission.setStatus(isApproved ? Status.ACCEPTE : Status.REFUSE_ADMIN);
                MissionRequest updatedMission = missionRequestRepository.save(mission);
                return modelMapper.map(updatedMission, MissionRequestDTO.class);
            }
            throw new RuntimeException("Invalid status for admin decision");
        }
        throw new RuntimeException("MissionRequest with ID " + missionId + " not found");
    }

    public boolean hasPendingRequest(Long workerId) {
        return missionRequestRepository.countByWorkerIdAndStatusIn(
                workerId, List.of(Status.EN_ATTENTE_RH, Status.EN_ATTENTE_ADMIN)) > 0;
    }
}
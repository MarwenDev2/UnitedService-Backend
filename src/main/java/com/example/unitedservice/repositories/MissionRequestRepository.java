package com.example.unitedservice.repositories;

import com.example.unitedservice.entities.MissionRequest;
import com.example.unitedservice.entities.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MissionRequestRepository extends JpaRepository<MissionRequest, Long> {
    List<MissionRequest> findByStatus(Status status);
    long countByStatus(Status status);
    long countByWorkerIdAndStatusIn(Long workerId, List<Status> statuses);

    // Optional: Add if needed for decision-based filtering
    // List<MissionRequest> findByRhDecision_Approved(Boolean approved);
}
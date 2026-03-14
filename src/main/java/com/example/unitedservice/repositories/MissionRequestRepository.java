package com.example.unitedservice.repositories;

import com.example.unitedservice.entities.MissionRequest;
import com.example.unitedservice.entities.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MissionRequestRepository extends JpaRepository<MissionRequest, Long> {
    List<MissionRequest> findByStatus(Status status);

    long countByStatus(Status status);

    // ✅ updated for ManyToMany
    long countByWorkers_IdAndStatusIn(Long workerId, List<Status> statuses);

    @Query("SELECT mr FROM MissionRequest mr LEFT JOIN FETCH mr.workers WHERE mr.id = :id")
    Optional<MissionRequest> findByIdWithWorkers(@Param("id") Long id);

    // Optional: Add if needed for decision-based filtering
    // List<MissionRequest> findByRhDecision_Approved(Boolean approved);
}

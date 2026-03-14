package com.example.unitedservice.repositories;
import com.example.unitedservice.entities.DemandeAvance;
import com.example.unitedservice.entities.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface DemandeAvanceRepository extends JpaRepository<DemandeAvance, Long> {
    List<DemandeAvance> findByStatus(Status status);

    long countByStatus(Status status);

    long countByWorkerIdAndStatusIn(Long workerId, List<Status> statuses);
}
package com.example.unitedservice.repositories;

import com.example.unitedservice.entities.DemandeConge;
import com.example.unitedservice.entities.Status;
import com.example.unitedservice.entities.TypeConge;
import com.example.unitedservice.entities.Worker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface DemandeCongeRepository extends JpaRepository<DemandeConge, Long> {
    List<DemandeConge> findByWorker(Worker worker);
    List<DemandeConge> findByStatus(Status status);

    @Query("SELECT COUNT(d) FROM DemandeConge d WHERE d.status = :status")
    int countByStatus(@Param("status") Status status);

    @Query("SELECT COUNT(d) FROM DemandeConge d WHERE d.type = :type")
    int countByType(@Param("type") TypeConge type);

    @Query("SELECT COUNT(d) FROM DemandeConge d WHERE d.status IN :statuses AND d.worker.id = :workerId")
    int countByWorkerIdAndStatusIn(@Param("workerId") Long workerId, @Param("statuses") List<Status> statuses);

    @Query("SELECT COUNT(d) FROM DemandeConge d WHERE FUNCTION('MONTH', d.dateDemande) = :month AND FUNCTION('YEAR', d.dateDemande) = :year")
    int countByMonth(@Param("month") int month, @Param("year") int year);

    @Query("SELECT d FROM DemandeConge d WHERE d.dateDemande >= :since ORDER BY d.dateDemande DESC")
    List<DemandeConge> findRecentConge(@Param("since") LocalDate since);
}

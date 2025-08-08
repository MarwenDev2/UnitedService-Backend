package com.example.unitedservice.repositories;

import com.example.unitedservice.entities.Worker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkerRepository extends JpaRepository<Worker, Long> {
    boolean existsByCin(String cin);

    Optional<Worker> findByCin(String cin);
}

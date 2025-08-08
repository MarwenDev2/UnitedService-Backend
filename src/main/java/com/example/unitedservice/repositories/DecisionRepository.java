package com.example.unitedservice.repositories;

import com.example.unitedservice.entities.Decision;
import com.example.unitedservice.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DecisionRepository extends JpaRepository<Decision, Integer> {
    List<Decision> findByDecisionBy(User user);
}

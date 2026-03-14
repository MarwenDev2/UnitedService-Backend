package com.example.unitedservice.services;

import com.example.unitedservice.entities.*;
import com.example.unitedservice.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DecisionService {
    private final DecisionRepository decisionRepository;

    public List<Decision> getAllDecisions() {
        return decisionRepository.findAll();
    }

    public List<Decision> getByUser(User user) {
        return decisionRepository.findByDecisionBy(user);
    }

    public Decision saveDecision(Decision decision) {
        return decisionRepository.save(decision);
    }

    public Optional<Decision> getDecisionById(int id) {
        return decisionRepository.findById(id);
    }

    public void deleteDecision(int id) {
        decisionRepository.deleteById(id);
    }
}

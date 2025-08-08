package com.example.unitedservice.controllers;

import com.example.unitedservice.dto.DecisionDTO;
import com.example.unitedservice.entities.Decision;
import com.example.unitedservice.services.DecisionService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/decisions")
@RequiredArgsConstructor
class DecisionController {
    private final DecisionService decisionService;
    private final ModelMapper modelMapper;

    @GetMapping
    public List<DecisionDTO> getAll() {
        return decisionService.getAllDecisions().stream()
                .map(decision -> modelMapper.map(decision, DecisionDTO.class))
                .collect(Collectors.toList());
    }

    @PostMapping
    public DecisionDTO create(@RequestBody DecisionDTO decisionDTO) {
        Decision decision = modelMapper.map(decisionDTO, Decision.class);
        Decision savedDecision = decisionService.saveDecision(decision);
        return modelMapper.map(savedDecision, DecisionDTO.class);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DecisionDTO> getById(@PathVariable int id) {
        return decisionService.getDecisionById(id)
                .map(decision -> modelMapper.map(decision, DecisionDTO.class))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
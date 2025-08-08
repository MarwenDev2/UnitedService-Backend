package com.example.unitedservice.controllers;

import com.example.unitedservice.dto.WorkerDTO;
import com.example.unitedservice.entities.Worker;
import com.example.unitedservice.services.ImageStorageService;
import com.example.unitedservice.services.WorkerService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/workers")
@RequiredArgsConstructor
class WorkerController {
    private final WorkerService workerService;
    private final ImageStorageService fileStorageService;
    private final ModelMapper modelMapper;

    @GetMapping
    public List<WorkerDTO> getAllWorkers() {
        return workerService.getAllWorkers().stream()
                .map(worker -> modelMapper.map(worker, WorkerDTO.class))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkerDTO> getWorker(@PathVariable Long id) {
        return workerService.getWorkerById(id)
                .map(worker -> modelMapper.map(worker, WorkerDTO.class))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cin/{cin}")
    public ResponseEntity<WorkerDTO> getWorkerByCin(@PathVariable String cin) {
        return workerService.getWorkerByCin(cin)
                .map(worker -> modelMapper.map(worker, WorkerDTO.class))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/photo")
    public ResponseEntity<String> getWorkerPhoto(@PathVariable Long id) {
        String photoPath = workerService.getWorkerPhotoPath(id);
        return ResponseEntity.ok(photoPath);
    }

    @PostMapping("/{id}/photo")
    public ResponseEntity<WorkerDTO> uploadWorkerPhoto(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        Worker updatedWorker = workerService.uploadWorkerPhoto(id, file);
        return ResponseEntity.ok(modelMapper.map(updatedWorker, WorkerDTO.class));
    }

    @GetMapping("/photo/{fileName}")
    public ResponseEntity<Resource> serveWorkerPhoto(@PathVariable String fileName) {
        Resource file = fileStorageService.loadFile(fileName);
        return ResponseEntity.ok()
                .contentType(MediaTypeFactory.getMediaType(file).orElse(MediaType.APPLICATION_OCTET_STREAM))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }

    @PostMapping
    public WorkerDTO createWorker(@RequestBody WorkerDTO workerDTO) {
        Worker worker = modelMapper.map(workerDTO, Worker.class);
        Worker savedWorker = workerService.saveWorker(worker);
        return modelMapper.map(savedWorker, WorkerDTO.class);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkerDTO> updateWorker(@PathVariable Long id, @RequestBody WorkerDTO workerDTO) {
        if (!workerService.getWorkerById(id).isPresent()) return ResponseEntity.notFound().build();
        Worker worker = modelMapper.map(workerDTO, Worker.class);
        worker.setId(id);
        Worker updatedWorker = workerService.saveWorker(worker);
        return ResponseEntity.ok(modelMapper.map(updatedWorker, WorkerDTO.class));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorker(@PathVariable Long id) {
        workerService.deleteWorker(id);
        return ResponseEntity.noContent().build();
    }
}
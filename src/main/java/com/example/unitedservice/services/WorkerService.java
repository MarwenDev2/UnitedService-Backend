package com.example.unitedservice.services;

import com.example.unitedservice.entities.*;
import com.example.unitedservice.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class WorkerService {
    private final WorkerRepository workerRepository;
    private final ImageStorageService fileStorageService;

    public List<Worker> getAllWorkers() {
        return workerRepository.findAll();
    }

    public Optional<Worker> getWorkerById(Long id) {
        return workerRepository.findById(id);
    }

    public Optional<Worker> getWorkerByCin(String cin) {
        return workerRepository.findByCin(cin);
    }

    public boolean existsByCin(String cin) {
        return workerRepository.existsByCin(cin);
    }

    public Worker saveWorker(Worker worker) {
        return workerRepository.save(worker);
    }

    public Worker updateWorker(long id, Worker worker) {
        return workerRepository.save(worker);
    }

    public void deleteWorker(Long id) {
        workerRepository.deleteById(id);
    }

    public String getWorkerPhotoPath(Long id) {
        Optional<Worker> worker = workerRepository.findById(id);
        if (worker.isPresent() && worker.get().getProfileImagePath() != null) {
            return "/Users/" + worker.get().getProfileImagePath();
        }
        return worker.map(w -> w.getGender().toLowerCase().equals("femme")
                ? "/Users/default-female.png"
                : "/Users/default-male.png").orElse("/Users/default-male.png");
    }

    public Worker uploadWorkerPhoto(Long id, MultipartFile file) {
        Optional<Worker> optionalWorker = workerRepository.findById(id);
        if (!optionalWorker.isPresent()) {
            throw new RuntimeException("Worker with ID " + id + " not found");
        }
        if (!file.getContentType().startsWith("image/")) {
            throw new RuntimeException("Only image files are allowed");
        }

        String fileName = fileStorageService.storeFile(file);
        Worker worker = optionalWorker.get();
        worker.setProfileImagePath(fileName);
        return workerRepository.save(worker);
    }
}
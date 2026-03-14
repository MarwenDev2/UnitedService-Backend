package com.example.unitedservice.services;

import com.example.unitedservice.entities.*;
import com.example.unitedservice.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
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


    public void deleteWorker1(Long workerId) {
        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new RuntimeException("Worker not found"));

        // 🔥 Remove worker from mission requests
        if (worker.getMissionRequests() != null) {
            for (MissionRequest mr : worker.getMissionRequests()) {
                mr.getWorkers().remove(worker);
            }
            worker.getMissionRequests().clear();
        }

        workerRepository.delete(worker);
    }


    public String getWorkerPhotoPath(Long id) {
        Optional<Worker> worker = workerRepository.findById(id);

        if (worker.isPresent() && worker.get().getProfileImagePath() != null) {
            return worker.get().getProfileImagePath();
        }

        return "/uploads/photos/" +
                (worker.isPresent() &&
                        "femme".equalsIgnoreCase(worker.get().getGender())
                        ? "default-female.png"
                        : "default-male.png");
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
        worker.setProfileImagePath("/uploads/photos/" + fileName);
        return workerRepository.save(worker);
    }

    public Worker uploadRelatedFiles(Long id, MultipartFile[] files) {
        Optional<Worker> optionalWorker = workerRepository.findById(id);
        if (!optionalWorker.isPresent()) {
            throw new RuntimeException("Worker with ID " + id + " not found");
        }

        Worker worker = optionalWorker.get();
        StringBuilder relatedFiles = new StringBuilder();
        if (worker.getRelatedFilesPath() != null && !worker.getRelatedFilesPath().isEmpty()) {
            relatedFiles.append(worker.getRelatedFilesPath()).append(";");
        }

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String fileName = fileStorageService.storeFile(file);
                if (relatedFiles.length() > 0) {
                    relatedFiles.append(";");
                }
                relatedFiles.append(fileName);
            }
        }

        worker.setRelatedFilesPath(relatedFiles.toString());
        return workerRepository.save(worker);
    }

    @Scheduled(cron = "0 0 0 1 * ?")
    public void updateMonthlyCongeCredits() {
        List<Worker> workers = workerRepository.findAll();
        LocalDate now = LocalDate.now();

        for (Worker worker : workers) {
            // Check if credit was already updated this month
            if (worker.getLastCongeCreditUpdate() == null ||
                    worker.getLastCongeCreditUpdate().getMonthValue() != now.getMonthValue() ||
                    worker.getLastCongeCreditUpdate().getYear() != now.getYear()) {

                worker.setTotalCongeDays(worker.getTotalCongeDays() + 2);
                worker.setLastCongeCreditUpdate(now);
                workerRepository.save(worker);
            }
        }
    }
}
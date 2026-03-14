package com.example.unitedservice.repositories;

import com.example.unitedservice.entities.PushSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface PushSubscriptionRepository extends JpaRepository<PushSubscription, Long> {

    List<PushSubscription> findByEndpoint(String endpoint);

    List<PushSubscription> findByUserId(Long userId);

    List<PushSubscription> findByUserIdAndActiveTrue(Long userId);

    List<PushSubscription> findByActiveTrue();

    @Query("SELECT ps FROM PushSubscription ps WHERE ps.userId = ?1 AND ps.endpoint = ?2")
    Optional<PushSubscription> findByUserIdAndEndpoint(Long userId, String endpoint);

    @Transactional
    @Modifying
    @Query("DELETE FROM PushSubscription ps WHERE ps.endpoint = ?1")
    void deleteByEndpoint(String endpoint);

    @Transactional
    @Modifying
    @Query("UPDATE PushSubscription ps SET ps.active = false WHERE ps.endpoint = ?1")
    void deactivateByEndpoint(String endpoint);
}
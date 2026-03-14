package com.example.unitedservice.entities;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "demande_avance")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DemandeAvance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "worker_id", nullable = false)
    private Worker worker;

    @Column(nullable = false)
    private double requestedAmount;

    @Column(nullable = false)
    private double adminResponseAmount = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status ;

    @Column
    private String adminComment;

    @Column(nullable = false)
    private LocalDate dateRequest = LocalDate.now();
}
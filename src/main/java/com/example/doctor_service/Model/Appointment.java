package com.example.doctor_service.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Entity
@Table(name="Appointment")
@Data
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column
    private LocalDateTime datetime;

    @Column(nullable = true) // null if appointment is not taken
    private Integer patientId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    @JsonBackReference
    private Doctor doctor;

    public Integer getDoctorId() {
        return (doctor != null) ? doctor.getId() : null;
    }
}

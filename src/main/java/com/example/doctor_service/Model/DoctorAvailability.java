package com.example.doctor_service.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name="DoctorAvailability")
@Data
public class DoctorAvailability {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    @JsonBackReference
    private Doctor doctor;

    @Column
    private LocalDate availableDate;

    public DoctorAvailability(Doctor doctor, LocalDate availableDate) {
        this.doctor = doctor;
        this.availableDate = availableDate;
    }

    public DoctorAvailability() {}

}

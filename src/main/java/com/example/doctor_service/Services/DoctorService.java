package com.example.doctor_service.Services;

import com.example.doctor_service.Kafka.Events.*;
import com.example.doctor_service.Model.Appointment;
import com.example.doctor_service.Model.Doctor;
import com.example.doctor_service.Repositories.DoctorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class DoctorService {
    final Logger logger = LoggerFactory.getLogger(DoctorService.class);

    private DoctorRepository doctorRepository;
    private AppointmentService appointmentService;

    @Value("${spring.kafka.topics.doctor}")
    private String doctorTopic;

    private final KafkaTemplate<String, KafkaEventWrapper> kafkaTemplate;

    @Autowired
    public DoctorService(DoctorRepository doctorRepository,
                         AppointmentService appointmentService,
                         KafkaTemplate<String, KafkaEventWrapper> kafkaTemplate) {
        this.doctorRepository = doctorRepository;
        this.appointmentService = appointmentService;
        this.kafkaTemplate = kafkaTemplate;
    }

    public Optional<Doctor> findById(int id) {
        return doctorRepository.findById(id);
    }

    public Doctor addDoctor(Doctor doctor) {
        logger.info("Sending message to kafka {}", doctor.getName());
        Doctor createdDoctor = doctorRepository.save(doctor);
        kafkaTemplate.send(doctorTopic, new KafkaEventWrapper("DOCTOR_CREATED",
                doctor));
        return createdDoctor;
    }

    public void deleteDoctorById(int id) throws EntityNotFoundException {
        Optional<Doctor> doctor = doctorRepository.findById(id);
        if (doctor.isPresent()){
            doctorRepository.deleteById(id);
            kafkaTemplate.send(doctorTopic, new KafkaEventWrapper("DOCTOR_DELETED", doctor));
        } else {
            throw new EntityNotFoundException("Doctor with id " + id + " not found");
        }
    }

    public Iterable<Doctor> getDoctors() {
        return doctorRepository.findAll();
    }
}

package com.example.doctor_service.Services;

import com.example.doctor_service.Kafka.Events.KafkaEventWrapper;
import com.example.doctor_service.Model.Doctor;
import com.example.doctor_service.Model.DoctorAvailability;
import com.example.doctor_service.Repositories.AppointmentRepository;
import com.example.doctor_service.Repositories.DoctorAvailabilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.Local;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Service
public class DoctorAvailabilityService {
    private DoctorAvailabilityRepository doctorAvailabilityRepository;

    @Value("${spring.kafka.topics.doctor}")
    private String doctorTopic;

    private final KafkaTemplate<String, KafkaEventWrapper> kafkaTemplate;

    @Autowired
    public DoctorAvailabilityService(DoctorAvailabilityRepository doctorAvailabilityRepository,
                                     KafkaTemplate<String, KafkaEventWrapper> kafkaTemplate) {
        this.doctorAvailabilityRepository = doctorAvailabilityRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public List<LocalDate> getFilteredDoctorAvailability(int doctorId, LocalDate startDate, LocalDate endDate) {
        return doctorAvailabilityRepository.findAvailableDatesByDoctorIdAndDateRange(doctorId, startDate, endDate);
    }

    public List<LocalDate> getWholeDoctorAvailability(int doctorId) {
        return doctorAvailabilityRepository.findAvailableDatesByDoctorId(doctorId);
    }

    public void addDoctorAvailability(Doctor doctor, LocalDate availableDate) {
        doctorAvailabilityRepository.save(new DoctorAvailability(doctor, availableDate));
        Map<String, String> response = new HashMap<>();
        response.put("availabilityDate", String.valueOf(availableDate));
        response.put("doctorId", String.valueOf(doctor.getId()));
        kafkaTemplate.send(doctorTopic, new KafkaEventWrapper("AVAILABILITY_ADDED", response));
    }

    public void deleteAvailabilityDate(Doctor doctor, LocalDate availableDate) {
        doctorAvailabilityRepository.deleteAvailability(doctor, availableDate);
        Map<String, String> response = new HashMap<>();
        response.put("availabilityDate", String.valueOf(availableDate));
        response.put("doctorId", String.valueOf(doctor.getId()));
        kafkaTemplate.send(doctorTopic, new KafkaEventWrapper("AVAILABILITY_DELETED", response));
    }
}

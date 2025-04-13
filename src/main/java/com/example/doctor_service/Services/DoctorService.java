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

    public void deleteDoctorById(int id) {
        doctorRepository.deleteById(id);
        kafkaTemplate.send(doctorTopic, new KafkaEventWrapper("DOCTOR_DELETED", id));
    }

    /* by doctor/admin */
    public Appointment createNewAppointment(Appointment appointment, int doctorId) {
        Optional<Doctor> doctor = findById(doctorId);
        if (doctor.isPresent()) {
            Doctor doc = doctor.get();
            Appointment newAppointment = new Appointment();
            newAppointment.setDatetime(appointment.getDatetime());
            newAppointment.setPatientId(appointment.getPatientId());
            newAppointment.setDoctor(doc);

            doc.getAppointments().add(newAppointment);
            doctorRepository.save(doc);
            appointmentService.addAppointment(newAppointment);

            kafkaTemplate.send(doctorTopic, new KafkaEventWrapper("APPOINTMENT_CREATED", newAppointment));
            return newAppointment;
        } else throw new EntityNotFoundException("Doctor not found, so impossible to add new appointment");
    }

    /* by doctor or admin */
    public void deleteAppointment(int appointmentId, int doctorId) throws Exception {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new EntityNotFoundException("Doctor not found with id: " + doctorId));

        // Find the appointment
        Appointment appointmentToDelete = appointmentService.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found with id: " + appointmentId));

        // Verify the appointment belongs to this doctor
        if (!doctor.getAppointments().contains(appointmentToDelete)) {
            throw new IllegalArgumentException("Appointment does not belong to the specified doctor");
        }

        // Remove the appointment from the doctor's collection
        doctor.getAppointments().remove(appointmentToDelete);

        // Delete the appointment
        appointmentService.deleteAppointmentById(appointmentToDelete.getId());

        doctorRepository.save(doctor);

        Map<String, String> response = new HashMap<>();
        response.put("appointmentId", String.valueOf(appointmentId));
        response.put("doctorId", String.valueOf(doctorId));
        kafkaTemplate.send(doctorTopic, new KafkaEventWrapper("APPOINTMENT_CANCELED", response));
    }

    public Iterable<Doctor> getDoctors() {
        return doctorRepository.findAll();
    }
}

package com.example.doctor_service.Services;

import com.example.doctor_service.Model.Appointment;
import com.example.doctor_service.Model.Doctor;
import com.example.doctor_service.Repositories.DoctorRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DoctorService {
    private DoctorRepository doctorRepository;
    private AppointmentService appointmentService;

    @PersistenceContext
    private EntityManager entityManager; // Inject EntityManager

    @Autowired
    public DoctorService(DoctorRepository doctorRepository, AppointmentService appointmentService) {
        this.doctorRepository = doctorRepository;
        this.appointmentService = appointmentService;
    }

    public Optional<Doctor> findById(int id) {
        return doctorRepository.findById(id);
    }

    public Doctor addDoctor(Doctor doctor) {
        return doctorRepository.save(doctor);
    }

    public void deleteDoctorById(int id) {
        doctorRepository.deleteById(id);
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

        // Optional: save the doctor to ensure the collection is updated
        doctorRepository.save(doctor);
    }

    public Iterable<Doctor> getDoctors() {
        return doctorRepository.findAll();
    }
}

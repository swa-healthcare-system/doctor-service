package com.example.doctor_service.Services;

import com.example.doctor_service.Model.Appointment;
import com.example.doctor_service.Repositories.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AppointmentService {
    private AppointmentRepository appointmentRepository;

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public Optional<Appointment> findById(int id) {
        return appointmentRepository.findById(id);
    }

    public Appointment addAppointment(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }

    public void deleteAppointmentById(int id) {
        appointmentRepository.deleteById(id);
    }


    public Appointment updateAppointment(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }

//    /* by patient */
//    public void handleAppointmentReservation(int appointmentId, int patientId) {
//        Optional<Appointment> appointmentOptional = this.findById(appointmentId);
//        if (appointmentOptional.isPresent()) {
//            Appointment appointment = appointmentOptional.get();
//            if (appointment.getPatientId() == null) {
//                appointment.setPatientId(patientId);
//                this.updateAppointment(appointment);
//            }
//        }
//    }
//
//    /* by patient */
//    public void handleAppointmentCancellation(int appointmentId) {
//        Optional<Appointment> appointmentOptional = this.findById(appointmentId);
//        if (appointmentOptional.isPresent()) {
//            Appointment appointment = appointmentOptional.get();
//            appointment.setPatientId(null);
//            this.updateAppointment(appointment);
//        }
//    }

}

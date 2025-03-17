package com.example.doctor_service.Dto.Responses;

import com.example.doctor_service.Model.Appointment;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class DoctorDtoResponse {
    private Integer id;

    private String name;

    private String surname;

    private List<Appointment> appointments;

//    public List<Appointment> getAppointments() {
//        return appointments.stream().map(appointment -> { });
//    }
//
//    public Appointment getAppointmentWithoutCircularReference(Appointment appointment) {
//        Appointment appointment1 = new Appointment();
//        appointment1.setId(appointment.getId());
//        appointment1.setPatientId(appointment.getPatientId());
//        appointment1.setDatetime(appointment.getDatetime());
//        appointment1.setDoctor();
//    }
}

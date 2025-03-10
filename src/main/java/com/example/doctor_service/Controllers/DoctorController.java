package com.example.doctor_service.Controllers;

import com.example.doctor_service.Model.Appointment;
import com.example.doctor_service.Model.Doctor;
import com.example.doctor_service.Repositories.DoctorRepository;
import com.example.doctor_service.Services.AppointmentService;
import com.example.doctor_service.Services.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

@RestController
public class DoctorController {
    DoctorService doctorService;
    AppointmentService appointmentService;

    @Autowired
    public DoctorController(DoctorService doctorService, AppointmentService appointmentService){
        this.doctorService = doctorService;
        this.appointmentService = appointmentService;
    }

    @GetMapping("/doctor/{id}/{month}/{year}")
    public ResponseEntity<Doctor> getDoctorByID(@PathVariable int id, @PathVariable String month, @PathVariable String year) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String inputDate = year + "-" + month + "-" + "01";
        Date date = formatter.parse(inputDate);
//        Date YearMonth.of(2000, 2);
        Optional<Doctor> res = doctorService.findById(id);
        if (res.isPresent()) {
            Doctor doctor = res.get();
            doctor.setAppointments(doctor.getAppointments()); // TODO: filter by date
            return ResponseEntity.ok(res.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/doctor")
    public void addDoctor(@RequestBody Doctor doctor){
        doctorService.addDoctor(doctor);
    }

    @DeleteMapping("doctor/{id}")
    public void deleteDoctorById(@PathVariable("id") int id){
        doctorService.deleteDoctorById(id);
    }


    @GetMapping("/appointment/{id}")
    public ResponseEntity<Appointment> getAppointmentByID(@PathVariable int id){
        Optional<Appointment> res = appointmentService.findById(id);
        if (res.isPresent()) {
            return ResponseEntity.ok(res.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/doctor/{doctor_id}/appointment/")
    public void addAppointmentSlot(@RequestBody Appointment appointment, @PathVariable("doctor_id") int doctorId){
        doctorService.createNewAppointment(appointment, doctorId);
    }


    @PostMapping("/doctor/{doctor_id}/appointment/{appointment_id}")
    public void deleteAppointmentSlot(@PathVariable("doctor_id") int doctorId, @PathVariable("appointment_id") int appointmentId){
        try {
            doctorService.deleteAppointment(appointmentId, doctorId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //
//    @PostMapping
//    public void handleAppointmentReservation(@PathVariable("appointment_id") int appointmentId,
//                                             @PathVariable("patient_id") int patientId){
//        appointmentService.handleAppointmentReservation(appointmentId, patientId);
//    }
//
//    @DeleteMapping("{appointment_id}")
//    public void handleAppointmentCancellationById(@PathVariable("appointment_id") int appointmentId){
//        appointmentService.handleAppointmentCancellation(appointmentId);
//    }

}

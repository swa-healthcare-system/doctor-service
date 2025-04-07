package com.example.doctor_service.Controllers;

import com.example.doctor_service.Exceptions.ErrorResponse;
import com.example.doctor_service.Model.Appointment;
import com.example.doctor_service.Model.Doctor;
import com.example.doctor_service.Repositories.DoctorRepository;
import com.example.doctor_service.Services.AppointmentService;
import com.example.doctor_service.Services.DoctorService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.Column;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.web.ErrorResponse;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/api/doctors")public class DoctorController {

    final Logger logger = LoggerFactory.getLogger(DoctorController.class);

    DoctorService doctorService;
    AppointmentService appointmentService;
    public record DoctorDateFilter(int month, int year) {}
    public record DoctorDtoResponse(Integer id, String name, String surname ,List<Appointment>appointments) {}


    @Autowired
    public DoctorController(DoctorService doctorService, AppointmentService appointmentService){
        this.doctorService = doctorService;
        this.appointmentService = appointmentService;
    }

    @Operation(description = "Gets a doctor by id. Returns a doctor with appointments in a particular month and year.")
    @PostMapping("/{id}")
    public ResponseEntity<DoctorDtoResponse> getDoctorByID(@PathVariable int id, @RequestBody DoctorDateFilter doctorDateFilter) throws ParseException {
        logger.info("Get doctor by id with filtered appointments request: id: {}, month: {}, year:{} .",
                id, doctorDateFilter.month, doctorDateFilter.year);

        try {
            YearMonth yearMonth = YearMonth.of(doctorDateFilter.year, doctorDateFilter.month);

            LocalDate firstDayOfMonth = yearMonth.atDay(1);
            LocalDate endOfMonth = yearMonth.atEndOfMonth();

            Optional<Doctor> res = doctorService.findById(id);
            if (res.isPresent()) {
                Doctor doctor = res.get();
                doctor.setAppointments(doctor.getAppointments().stream().filter(a ->
                        !a.getDatetime().toLocalDate().isBefore(firstDayOfMonth) && !a.getDatetime().toLocalDate().isAfter(endOfMonth))
                        .collect(Collectors.toList()));
                Doctor resDoctor = res.get();
                return ResponseEntity.ok(new DoctorDtoResponse(
                        resDoctor.getId(),
                        resDoctor.getName(),
                        resDoctor.getSurname(),
                        resDoctor.getAppointments()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (DateTimeException dateTimeException) {
            logger.error("Failed to parse date. month: {}, year: {}. Message: {}", doctorDateFilter.month, doctorDateFilter.year, dateTimeException.getMessage());
            throw new DateTimeException("Failed to parse month or year: " + dateTimeException.getMessage());
        }
    }

    @Operation(description = "Returns all doctors.")
    @GetMapping()
    public ResponseEntity<List<DoctorDtoResponse>> getDoctors(){
        logger.info("Getting all doctors with complete lists of appointments.");
        try {
            Iterable<Doctor> doctors = doctorService.getDoctors();
            return ResponseEntity.ok(StreamSupport.stream(doctors.spliterator(), false).map(doctor -> new DoctorDtoResponse(
                    doctor.getId(),
                    doctor.getName(),
                    doctor.getSurname(),
                    doctor.getAppointments())).collect(Collectors.toList())
            );
        } catch (Exception e){
            logger.error("Failed to get all doctors. Message: {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @Operation(description = "Adds new doctor.")
    @PostMapping()
    public ResponseEntity<DoctorDtoResponse> addDoctor(@RequestBody Doctor doctor){
        logger.info("Adding doctor: {}", doctor.toString());
        try {
            Doctor createdDoctor = doctorService.addDoctor(doctor);
            return ResponseEntity.ok(new DoctorDtoResponse(
                    createdDoctor.getId(),
                    createdDoctor.getName(),
                    createdDoctor.getSurname(),
                    createdDoctor.getAppointments()));
        } catch (Exception e){
            logger.error("Failed to add doctor. Message: {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @Operation(description = "Delete doctor by id including all doctor's appointments.")
    @DeleteMapping("/{id}")
    public void deleteDoctorById(@PathVariable("id") int id){
        doctorService.deleteDoctorById(id);
    }

    @Operation(description = "Gets appointment by id.")
    @GetMapping("/appointment/{id}")
    public ResponseEntity<Appointment> getAppointmentByID(@PathVariable int id){
        logger.info("Getting appointment with id {}.", id);
        Optional<Appointment> res = appointmentService.findById(id);
        if (res.isPresent()) {
            return ResponseEntity.ok(res.get());
        } else {
            logger.error("Appointment with id {} was not found.", id);
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(description = "Adds appointment lot to a doctor by doctor's id.")
    @PostMapping("/doctor/{doctor_id}/appointment/")
    public ResponseEntity<Appointment> addAppointmentSlot(@RequestBody Appointment appointment, @PathVariable("doctor_id") int doctorId){
        logger.info("Adding appointment slot to the doctor with id {}.", doctorId);
        try {
            Appointment createdAppointment =  doctorService.createNewAppointment(appointment, doctorId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdAppointment);
        } catch (EntityNotFoundException e) {
            logger.error("Doctor with id {} was not found.", doctorId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(description = "Deletes appointment slot from the doctor by doctor's id and id of the appointment.")
    @DeleteMapping("/doctor/{doctor_id}/appointment/{appointment_id}")
    public void deleteAppointmentSlot(@PathVariable("doctor_id") int doctorId, @PathVariable("appointment_id") int appointmentId){
        logger.info("Deleting appointment slot to the doctor with id {}.", doctorId);
        try {
            doctorService.deleteAppointment(appointmentId, doctorId);
        } catch (Exception e) {
            logger.error("Failed to delete appointment slot.");
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

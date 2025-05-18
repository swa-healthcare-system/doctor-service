package com.example.doctor_service.Controllers;

import com.example.doctor_service.Model.Appointment;
import com.example.doctor_service.Model.Doctor;
import com.example.doctor_service.Model.DoctorAvailability;
import com.example.doctor_service.Services.AppointmentService;
import com.example.doctor_service.Services.DoctorAvailabilityService;
import com.example.doctor_service.Services.DoctorService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    final Logger logger = LoggerFactory.getLogger(DoctorController.class);

    DoctorService doctorService;
    DoctorAvailabilityService doctorAvailabilityService;
    public record DoctorDateFilter(int month, int year) {}
    public record DoctorDtoResponse(Integer id, String name, String surname, String email, String telephoneNumber, List<LocalDate> doctorAvailabilities) {}


    @Autowired
    public DoctorController(DoctorService doctorService, DoctorAvailabilityService doctorAvailabilityService){
        this.doctorService = doctorService;
        this.doctorAvailabilityService = doctorAvailabilityService;
    }

    @Operation(description = "Gets a doctor by id. Returns a doctor with doctorAvailabilities in a particular month and year.")
    @PostMapping("/{id}")
    public ResponseEntity<DoctorDtoResponse> getDoctorByID(@PathVariable int id, @RequestBody DoctorDateFilter doctorDateFilter) throws ParseException {
        logger.info("Get doctor by id with filtered doctorAvailabilities request: id: {}, month: {}, year:{} .",
                id, doctorDateFilter.month, doctorDateFilter.year);

        try {
            YearMonth yearMonth = YearMonth.of(doctorDateFilter.year, doctorDateFilter.month);

            LocalDate firstDayOfMonth = yearMonth.atDay(1);
            LocalDate endOfMonth = yearMonth.atEndOfMonth();

            Optional<Doctor> res = doctorService.findById(id);
            if (res.isPresent()) {
                Doctor doctor = res.get();
                DoctorDtoResponse doctorDtoResponse = new DoctorDtoResponse(
                        doctor.getId(),
                        doctor.getName(),
                        doctor.getSurname(),
                        doctor.getEmail(),
                        doctor.getTelephoneNumber(),
                        doctorAvailabilityService.getFilteredDoctorAvailability(id, firstDayOfMonth, endOfMonth));

                return ResponseEntity.ok(doctorDtoResponse);
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
        logger.info("Getting all doctors with complete lists of doctorAvailabilities.");
        try {
            Iterable<Doctor> doctors = doctorService.getDoctors();
            return ResponseEntity.ok(StreamSupport.stream(doctors.spliterator(), false).map(doctor -> new DoctorDtoResponse(
                    doctor.getId(),
                    doctor.getName(),
                    doctor.getSurname(),
                            doctor.getEmail(),
                    doctor.getTelephoneNumber(),
                    doctorAvailabilityService.getWholeDoctorAvailability(doctor.getId())))
                    .collect(Collectors.toList())
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
                    createdDoctor.getEmail(),
                    createdDoctor.getTelephoneNumber(),
                    new ArrayList<>()));
        } catch (Exception e){
            logger.error("Failed to add doctor. Message: {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @Operation(description = "Delete doctor by id including all doctor's doctorAvailabilities.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctorById(@PathVariable("id") int id) {
        try {
            doctorService.deleteDoctorById(id);
            return ResponseEntity.noContent().build(); // 204 No Content on success
        } catch (EntityNotFoundException entityNotFoundException) {
            logger.error("Failed to delete doctor. Message: {}", entityNotFoundException.getMessage());
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }

}

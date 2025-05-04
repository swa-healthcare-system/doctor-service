package com.example.doctor_service.Controllers;

import com.example.doctor_service.Model.Doctor;
import com.example.doctor_service.Services.DoctorAvailabilityService;
import com.example.doctor_service.Services.DoctorService;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/availability")
public class DoctorAvailabilityController {
    final Logger logger = LoggerFactory.getLogger(DoctorAvailabilityController.class);

    DoctorAvailabilityService doctorAvailabilityService;
    DoctorService doctorService;
    public record AvailabilityDtoRequest(Integer doctorId, List<LocalDate> doctorAvailabilities) {}

    @Autowired
    public DoctorAvailabilityController(DoctorService doctorService, DoctorAvailabilityService doctorAvailabilityService){
        this.doctorService = doctorService;
        this.doctorAvailabilityService = doctorAvailabilityService;
    }

    @Operation(description = "Adds new available date.")
    @PostMapping()
    public ResponseEntity addAvailability(@RequestBody AvailabilityDtoRequest availabilityDtoRequest){
        logger.info("Adding availability for {}", availabilityDtoRequest.doctorId);
        try {
            Optional<Doctor> doctorOptional = doctorService.findById(availabilityDtoRequest.doctorId);
            if (doctorOptional.isPresent()){
                Doctor doctor = doctorOptional.get();
                for (LocalDate availabilityDate : availabilityDtoRequest.doctorAvailabilities){
                    doctorAvailabilityService.addDoctorAvailability(doctor, availabilityDate);
                }
            }
            return ResponseEntity.status(200).build();
        } catch (Exception e){
            logger.error("Failed to add doctor. Message: {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @Operation(description = "Deletes available date.")
    @DeleteMapping()
    public ResponseEntity deleteAvailability(@RequestBody AvailabilityDtoRequest availabilityDtoRequest){
        logger.info("Deleting availability for {}", availabilityDtoRequest.doctorId);
        try {
            Optional<Doctor> doctorOptional = doctorService.findById(availabilityDtoRequest.doctorId);
            if (doctorOptional.isPresent()){
                Doctor doctor = doctorOptional.get();
                for (LocalDate availabilityDate : availabilityDtoRequest.doctorAvailabilities){
                    doctorAvailabilityService.deleteAvailabilityDate(doctor, availabilityDate);
                }
            }
            return ResponseEntity.status(200).build();
        } catch (Exception e){
            logger.error("Failed to add doctor. Message: {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }


}

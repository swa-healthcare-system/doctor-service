package com.example.doctor_service.Repositories;

import com.example.doctor_service.Model.Doctor;
import com.example.doctor_service.Model.DoctorAvailability;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DoctorAvailabilityRepository extends JpaRepository<DoctorAvailability, Integer> {

    @Query("SELECT da.availableDate FROM DoctorAvailability da WHERE da.doctor.id = :doctorId AND da.availableDate BETWEEN :startDate AND :endDate")
    List<LocalDate> findAvailableDatesByDoctorIdAndDateRange(@Param("doctorId") int doctorId,
                                                             @Param("startDate") LocalDate startDate,
                                                             @Param("endDate") LocalDate endDate);

    @Query("SELECT da.availableDate FROM DoctorAvailability da WHERE da.doctor.id = :doctorId")
    List<LocalDate> findAvailableDatesByDoctorId(@Param("doctorId") int doctorId);

    @Modifying
    @Transactional
    @Query("DELETE FROM DoctorAvailability da WHERE da.doctor = :doctor AND da.availableDate = :availabilityDate")
    void deleteAvailability(@Param("doctor") Doctor doctor,
                            @Param("availabilityDate") LocalDate availabilityDate);
}

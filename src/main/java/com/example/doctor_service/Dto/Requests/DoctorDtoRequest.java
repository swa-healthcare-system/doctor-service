package com.example.doctor_service.Dto.Requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.bind.annotation.PathVariable;

@Data
@AllArgsConstructor
public class DoctorDtoRequest {
    private int id;
    private int month;
    private int year;
}

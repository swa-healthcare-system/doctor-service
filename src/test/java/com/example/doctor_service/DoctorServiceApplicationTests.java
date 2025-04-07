package com.example.doctor_service;

import com.example.doctor_service.Model.Appointment;
import com.example.doctor_service.Model.Doctor;
import com.example.doctor_service.Repositories.DoctorRepository;
import com.example.doctor_service.Services.DoctorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Transactional
class DoctorServiceApplicationTests extends BaseIntegrationTest{

//	@Autowired
//	private MockMvc mockMvc;
//
//	@Autowired
//	private ObjectMapper objectMapper;
//
//	@Autowired
//	private DoctorRepository doctorRepository;
//
//	private Doctor testDoctor;
//	private Appointment testAppointment;
//
//	@BeforeEach
//	void setup() {
//
//		// Create a test doctor
//		testDoctor = new Doctor();
//		testDoctor.setName("John");
//		testDoctor.setSurname("Doe");
//		testDoctor.setAppointments(new ArrayList<>());
//
//		testDoctor = doctorRepository.save(testDoctor);
//
//		// Create a test appointment
//		testAppointment = new Appointment();
//		testAppointment.setPatientId(100);
//		testAppointment.setDatetime(LocalDateTime.of(2025, 3, 20, 10, 0));
//
//		// The appointment will be saved when needed in specific tests
//	}
//
//	@Test
//	void testGetAllDoctors() throws Exception {
//		mockMvc.perform(get("/api/doctors"))
//				.andExpect(status().isOk())
//				.andExpect(jsonPath("$", hasSize(1)))
//				.andExpect(jsonPath("$[0].id").value(testDoctor.getId()))
//				.andExpect(jsonPath("$[0].name").value("John"))
//				.andExpect(jsonPath("$[0].surname").value("Doe"));
//	}
//
//	@Test
//	void testGetDoctorById() throws Exception {
//		mockMvc.perform(get("/api/doctors"))
//				.andExpect(status().isOk())
//				.andExpect(jsonPath("$", hasSize(1)))
//				.andExpect(jsonPath("$[0].id").value(testDoctor.getId()))
//				.andExpect(jsonPath("$[0].name").value("John"))
//				.andExpect(jsonPath("$[0].surname").value("Doe"));
//	}
//

}

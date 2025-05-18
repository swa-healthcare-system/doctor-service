package com.example.doctor_service.integration;

import com.example.doctor_service.Controllers.DoctorController;
import com.example.doctor_service.Kafka.Events.KafkaEventWrapper;
import com.example.doctor_service.Model.Doctor;
import com.example.doctor_service.Model.DoctorAvailability;
import com.example.doctor_service.Repositories.DoctorAvailabilityRepository;
import com.example.doctor_service.Repositories.DoctorRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Testcontainers
public class DoctorIntegrationTests {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void configureTestDatabase(org.springframework.test.context.DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private DoctorAvailabilityRepository doctorAvailabilityRepository;

    @MockBean
    private KafkaTemplate<String, KafkaEventWrapper> kafkaTemplate;

    @BeforeEach
    void setUp() {
        doctorRepository.deleteAll();
    }

    Doctor getTestDoctor() {
        Doctor doctor = new Doctor();
        doctor.setName("John");
        doctor.setSurname("Doe");
        doctor.setEmail("john.doe@example.com");
        doctor.setTelephoneNumber("+1234567890");
        doctor.setAvailableDates(new ArrayList<>());

        return doctor;
    }

    String getTestDoctorDtoJson() {
        String doctorJson = """
            {
                "name": "Jane",
                "surname": "Smith",
                "email": "jane.smith@example.com",
                "telephoneNumber": "+0987654321",
                "availableDates": []
            }
            """;

        return doctorJson;
    }

    String getTestDoctorDtoJsonInvalid() {
        String doctorJson = """
            {
                "name": "",
                "surname": "Smith",
                "email": "invalid-email",
                "telephoneNumber": "+0987654321",
                "availableDates": []
            }
            """;

        return doctorJson;
    }

    List<Doctor> generateTestDoctors() {
        List<Doctor> doctors = new ArrayList<>();

        Doctor doctor1 = new Doctor();
        doctor1.setName("Alice");
        doctor1.setSurname("Johnson");
        doctor1.setEmail("alice.j@example.com");
        doctor1.setTelephoneNumber("+1122334455");
        doctor1.setAvailableDates(new ArrayList<>());

        Doctor doctor2 = new Doctor();
        doctor2.setName("Bob");
        doctor2.setSurname("Brown");
        doctor2.setEmail("bob.b@example.com");
        doctor2.setTelephoneNumber("+5566778899");
        doctor2.setAvailableDates(new ArrayList<>());

        Doctor doctor3 = new Doctor();
        doctor3.setName("Carol");
        doctor3.setSurname("White");
        doctor3.setEmail("carol.w@example.com");
        doctor3.setTelephoneNumber("+9988776655");
        doctor3.setAvailableDates(new ArrayList<>());

        doctors.add(doctor1);
        doctors.add(doctor2);
        doctors.add(doctor3);

        return doctors;
    }

    @Test
    void getDoctorsTest() throws Exception {
        List<Doctor> doctors = generateTestDoctors();
        doctorRepository.saveAll(doctors);

        mockMvc.perform(get("/api/doctors")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].name").value("Alice"))
                .andExpect(jsonPath("$[1].name").value("Bob"))
                .andExpect(jsonPath("$[2].name").value("Carol"));

        // Verify that doctors are correctly stored
        Iterable<Doctor> storedDoctors = doctorRepository.findAll();
        assertThat(storedDoctors).hasSize(3);
    }

    @Test
    void getDoctorExists() throws Exception {
        Doctor doctor = getTestDoctor();
        doctor = doctorRepository.save(doctor);

        DoctorAvailability availability = new DoctorAvailability();
        availability.setDoctor(doctor);
        availability.setAvailableDate(LocalDate.of(2025, 5, 15)); // any date in May
        doctorAvailabilityRepository.save(availability);

        DoctorController.DoctorDateFilter filter = new DoctorController.DoctorDateFilter(5, 2025);
        mockMvc.perform(post("/api/doctors/{id}", doctor.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filter)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(doctor.getId()))
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.surname").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.telephoneNumber").value("+1234567890"));
    }

    @Test
    void getDoctorNotExist() throws Exception {
        DoctorController.DoctorDateFilter filter = new DoctorController.DoctorDateFilter(5, 2025);
        mockMvc.perform(post("/api/doctors/{id}", 99999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filter)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteDoctorValid() throws Exception {
        Doctor doctor = doctorRepository.save(getTestDoctor());

        mockMvc.perform(delete("/api/doctors/" + doctor.getId()))
                .andExpect(status().isNoContent());

        assertFalse(doctorRepository.findById(doctor.getId()).isPresent());
    }

    @Test
    void deleteDoctorInvalid() throws Exception {
        mockMvc.perform(delete("/api/doctors/9999"))
                .andExpect(status().isNotFound());
    }
}
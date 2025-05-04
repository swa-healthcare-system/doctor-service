package com.example.doctor_service.Model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="Doctor")
@Data
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column
    private String name;

    @Column
    private String surname;

    @Column
    private String email;

    @Column
    private String telephoneNumber;

//    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL)
//    @JsonManagedReference
//    private List<DoctorAvailability> availableDates = new ArrayList<>();

    public Doctor(int id, String name, String surname, String email, String telephoneNumber) {
        this.id = id;
        this.name = name;
        this.surname= surname;
        this.email = email;
        this.telephoneNumber = telephoneNumber;
    }

    public Doctor() {}

    @Override
    public String toString() {
        return "Doctor{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
//                ", availableDates=" + availableDates +
                ", email=" + email +
                ", telephoneNumber=" + telephoneNumber +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

//    public List<DoctorAvailability> getAvailableDates() {
//        return availableDates;
//    }
//
//    public void setAvailableDates(List<DoctorAvailability> dates) {
//        this.availableDates = dates;
//    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

}

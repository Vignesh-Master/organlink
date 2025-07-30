package com.organlink.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a state/province in the location hierarchy
 */
@Entity
@Table(name = "states",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"name", "country_id"})
       },
       indexes = {
           @Index(name = "idx_state_country", columnList = "country_id")
       })
public class State extends BaseEntity {

    @NotBlank(message = "State name is required")
    @Size(min = 2, max = 100, message = "State name must be between 2 and 100 characters")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotBlank(message = "State code is required")
    @Size(min = 2, max = 10, message = "State code must be between 2 and 10 characters")
    @Column(name = "code", nullable = false, length = 10)
    private String code;

    @NotNull(message = "Country is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    @OneToMany(mappedBy = "state", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Hospital> hospitals = new ArrayList<>();

    // Constructors
    public State() {}

    public State(String name, String code, Country country) {
        this.name = name;
        this.code = code;
        this.country = country;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public List<Hospital> getHospitals() {
        return hospitals;
    }

    public void setHospitals(List<Hospital> hospitals) {
        this.hospitals = hospitals;
    }

    // Helper methods
    public void addHospital(Hospital hospital) {
        hospitals.add(hospital);
        hospital.setState(this);
    }

    public void removeHospital(Hospital hospital) {
        hospitals.remove(hospital);
        hospital.setState(null);
    }

    @Override
    public String toString() {
        return "State{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", country=" + (country != null ? country.getName() : null) +
                '}';
    }
}

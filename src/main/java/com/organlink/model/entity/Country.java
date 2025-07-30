package com.organlink.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a country in the location hierarchy
 */
@Entity
@Table(name = "countries", 
       uniqueConstraints = {
           @UniqueConstraint(columnNames = "name"),
           @UniqueConstraint(columnNames = "code")
       })
public class Country extends BaseEntity {

    @NotBlank(message = "Country name is required")
    @Size(min = 2, max = 100, message = "Country name must be between 2 and 100 characters")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Country code is required")
    @Size(min = 2, max = 3, message = "Country code must be between 2 and 3 characters")
    @Column(name = "code", nullable = false, length = 3)
    private String code;

    @OneToMany(mappedBy = "country", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<State> states = new ArrayList<>();

    // Constructors
    public Country() {}

    public Country(String name, String code) {
        this.name = name;
        this.code = code;
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
        this.code = code.toUpperCase();
    }

    public List<State> getStates() {
        return states;
    }

    public void setStates(List<State> states) {
        this.states = states;
    }

    // Helper methods
    public void addState(State state) {
        states.add(state);
        state.setCountry(this);
    }

    public void removeState(State state) {
        states.remove(state);
        state.setCountry(null);
    }

    @Override
    public String toString() {
        return "Country{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}

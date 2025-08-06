package com.organlink.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import com.fasterxml.jackson.annotation.JsonIgnore;

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
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true, exclude = {"states"})
@EqualsAndHashCode(callSuper = true, exclude = {"states"})
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
    @JsonIgnore
    private List<State> states = new ArrayList<>();

    // Custom constructor for convenience
    public Country(String name, String code) {
        this.name = name;
        this.code = code.toUpperCase();
    }

    // Custom setter to ensure uppercase code
    public void setCode(String code) {
        this.code = code.toUpperCase();
    }

    // Helper methods for bidirectional relationship management
    public void addState(State state) {
        states.add(state);
        // state.setCountry(this); // Will be enabled once State entity is fixed
    }

    public void removeState(State state) {
        states.remove(state);
        // state.setCountry(null); // Will be enabled once State entity is fixed
    }
}

package com.ezest.model;

import javax.validation.constraints.NotBlank;

import com.ezest.enumerators.VehicleType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Vehicle {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank(message = "Licence plate cannot be null")
	private String vehicleRegistrationNumber;
	
	@NotBlank(message = "Colour cannot be null")
	private String colour;
	
	@Enumerated(EnumType.STRING)
	@NotBlank(message = "Vehicle type cannot be null")
	private VehicleType vehicleType;
	

}

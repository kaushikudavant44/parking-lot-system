package com.ezest.model;


import javax.validation.constraints.NotBlank;

import com.ezest.enumerators.VehicleType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "PARKING_LOTS")
@Data
public class ParkingLot
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Lot name cannot be null")
    @Column(name = "LOT_NAME", unique = true)
    private String name;

    @NotBlank(message = "Lot floor cannot be null")
    @Column(name = "FLOOR")
    private String floor;

    @NotBlank(message = "Lot Type cannot be null")
    @Enumerated(EnumType.STRING)
    private VehicleType lotVehicleType;
    
    @NotBlank(message = "Lot Size cannot be null")
    private Long lotSize; 

    @Column(name = "LOT_PRICE")
    private double price;

}


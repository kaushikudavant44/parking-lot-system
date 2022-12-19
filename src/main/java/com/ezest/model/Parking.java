package com.ezest.model;

import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;

import com.ezest.enumerators.ParkingStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Table(name = "PARKING_RECORDS")
@Data
@AllArgsConstructor
public class Parking {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int parkingId;

	@NotBlank(message = "Lot name cannot be null")
	private String parkingLot;

	@NotBlank(message = "Licence plate cannot be null")
	private String vehicleRegistrationNumber;

	private LocalDateTime startDate;

	private LocalDateTime endDate;
	
	@Enumerated(EnumType.STRING)
	@NotBlank(message = "parkingStatus cannot be null")
	private ParkingStatus parkingStatus;

	private double price;
	
	public Parking() {
		
	}
	
	public Parking(String parkingLot, String vehicleRegistrationNumber, LocalDateTime startDate, LocalDateTime endDate,
			ParkingStatus paringStatus, double price) {
		this.parkingLot=parkingLot;
		this.vehicleRegistrationNumber=vehicleRegistrationNumber;
		this.startDate=startDate;
		this.endDate=endDate;
		this.parkingStatus=paringStatus;
		this.price=price;
	}

}

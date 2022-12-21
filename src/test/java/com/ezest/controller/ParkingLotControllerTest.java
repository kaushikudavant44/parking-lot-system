package com.ezest.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.ezest.enumerators.VehicleType;
import com.ezest.exceptions.ParkingLotNameAlreadyExistException;
import com.ezest.model.ParkingLot;
import com.ezest.service.ParkingLotService;

class ParkingLotControllerTest {

	@InjectMocks
	ParkingLotController parkingLotController;

	@Mock
	ParkingLotService parkingLotService;

	ParkingLot parkingLot;

	@BeforeEach
	void setUp() {

		MockitoAnnotations.openMocks(this);
		parkingLot = new ParkingLot();
		parkingLot.setName("Bike Lot");
		parkingLot.setLotVehicleType(VehicleType.BIKE);
		parkingLot.setLotSize(2L);
		parkingLot.setFloor("Ground Floor");
		parkingLot.setPrice(30L);
		
	}
	
	@Test
	@DisplayName("Get all parking Lots")
	void testCreateParkingLot() {
		ParkingLot notExistParkingLot=null;
		
		when(parkingLotService.findByName(parkingLot.getName())).thenReturn(Optional.ofNullable(notExistParkingLot));
		when(parkingLotService.createParkingLot(any(ParkingLot.class))).thenReturn(parkingLot);
		ResponseEntity<ParkingLot> responseEntity = parkingLotController.createParkingLot(parkingLot);
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
	}
	
	@Test
	@DisplayName("Throws error if  parking lot  already created with same name")
	void testCreateParkingLotWithExistingName() {
		Optional<ParkingLot> existingParkingLot = Optional.of((ParkingLot) parkingLot);
		ParkingLotNameAlreadyExistException thrown = assertThrows(
				ParkingLotNameAlreadyExistException.class,
		           () -> {when(parkingLotService.findByName(parkingLot.getName())).thenReturn(existingParkingLot);
		           parkingLotController.createParkingLot(parkingLot);
		           }
		    );
		Assertions.assertEquals(new ParkingLotNameAlreadyExistException().getMessage(),thrown.getMessage());
	}
	
	@Test
	@DisplayName("Get all parking lots")
	void testGetAllParkingLots() {
		List<ParkingLot> parkingLots = new ArrayList<>();
		when(parkingLotService.getParkingLots()).thenReturn(parkingLots);
		ResponseEntity<List<ParkingLot>> responseEntity = parkingLotController.getAllParkingLots();
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

}


/**
 * 
 */
package com.ezest.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.ezest.enumerators.ParkingStatus;
import com.ezest.enumerators.VehicleType;
import com.ezest.exceptions.VehicleNotFoundException;
import com.ezest.model.Parking;
import com.ezest.model.Vehicle;
import com.ezest.service.ParkingService;

/**
 * @author ADMIN
 *
 */
class ParkingControllerTest {
	
	@InjectMocks
	ParkingController parkingController;

	@Mock
	ParkingService parkingService;

	Parking parking;
	
	Vehicle vehicle;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);
		parking = new Parking();
		parking.setParkingLot("BIKE lot");
		parking.setVehicleRegistrationNumber("MH15HQ7720");
		parking.setParkingStatus(ParkingStatus.RESERVE);
		parking.setPrice(30);
		parking.setStartDate(LocalDateTime.now());
		parking.setEndDate(null);
		vehicle =new Vehicle();
		vehicle.setVehicleRegistrationNumber("MH15HQ7720");
		vehicle.setVehicleType(VehicleType.BIKE);
		vehicle.setColour("WHITE");
	}

	/**
	 * Test method for {@link com.ezest.controller.ParkingController#getAllParkingRecords()}.
	 */
	@Test
	void testGetAllParkingRecords() {
		
		List<Parking> parkings=new ArrayList<>();	
		parkings.add(parking);
		when(parkingService.getParkingList()).thenReturn(parkings);
		List<Parking> parkingResponse = parkingController.getAllParkingRecords();
		assertTrue(parkings.size() == parkingResponse.size() && parkings.containsAll(parkingResponse) && parkingResponse.containsAll(parkings));
	}

	/**
	 * Test method for {@link com.ezest.controller.ParkingController#getCurrentParkingRecords()}.
	 */
	@Test
	void testGetCurrentParkingRecords() {
		List<Parking> parkings=new ArrayList<>();	
		parkings.add(parking);
		Parking parking1 = new Parking();
		parking1.setParkingLot("BIKE lot");
		parking1.setVehicleRegistrationNumber("MH15HQ7720");
		parking1.setParkingStatus(ParkingStatus.RESERVE);
		parking1.setPrice(30);
		parking1.setStartDate(LocalDateTime.now());
		parking1.setEndDate(LocalDateTime.now().plusDays(2));
		parkings.add(parking1);
		when(parkingService.getParkingList()).thenReturn(parkings);
		parkings=parkings.stream().filter(p -> p.getEndDate()==null).collect(Collectors.toList());
		List<Parking> parkingResponse = parkingController.getCurrentParkingRecords();
		assertTrue(parkings.size() == parkingResponse.size() && parkings.containsAll(parkingResponse) && parkingResponse.containsAll(parkings));		
	}

	/**
	 * Test method for {@link com.ezest.controller.ParkingController#getCurrentParkingWithLicencePlate(java.lang.String)}.
	 */
	@Test
	void testGetCurrentParkingWithLicencePlate() {
		when(parkingService.getCurrentParkedVehicleByLicencePlate(parking.getVehicleRegistrationNumber())).thenReturn(parking);
		Parking currentParkingWithLicencePlate = parkingController.getCurrentParkingWithLicencePlate(parking.getVehicleRegistrationNumber());
		assertNotNull(currentParkingWithLicencePlate);
	}
	
	@Test
	void testGetCurrentParkingWithLicencePlateVehicleNotFound() {
		when(parkingService.getCurrentParkedVehicleByLicencePlate(any(String.class))).thenReturn(null);
		VehicleNotFoundException thrown = assertThrows(
				VehicleNotFoundException.class,
		           () -> 
		           parkingController.getCurrentParkingWithLicencePlate(parking.getVehicleRegistrationNumber())	           
		    );
		Assertions.assertEquals(new VehicleNotFoundException().getMessage(),thrown.getMessage());
	}
	

	/**
	 * Test method for {@link com.ezest.controller.ParkingController#checkInToParkingLot(com.ezest.model.Vehicle)}.
	 */
	@Test
	void testCheckInToParkingLot() {
		when(parkingService.createParkingRecord(vehicle)).thenReturn(parking);
		ResponseEntity<Parking> checkInToParkingLot = parkingController.checkInToParkingLot(vehicle);
		assertThat(checkInToParkingLot.getStatusCode()).isEqualTo(HttpStatus.CREATED);
	}

	/**
	 * Test method for {@link com.ezest.controller.ParkingController#checkOutFromParkingLot(int)}.
	 */
	@Test
	void testCheckOutFromParkingLot() {
		when(parkingService.updateParkingRecord(any(Integer.class))).thenReturn(parking);
		ResponseEntity<Parking> checkOutFromParkingLot = parkingController.checkOutFromParkingLot(any(Integer.class));
		assertThat(checkOutFromParkingLot.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

}

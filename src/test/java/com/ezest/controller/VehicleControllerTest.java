/**
 * 
 */
package com.ezest.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.ezest.enumerators.VehicleType;
import com.ezest.exceptions.VehicleAlreadyExistException;
import com.ezest.model.Vehicle;
import com.ezest.service.VehicleService;

/**
 * @author ADMIN
 *
 */
class VehicleControllerTest {
	
	@InjectMocks
	VehicleController vehicleController;
	
	@Mock
	VehicleService vehicleService;
	
	Vehicle vehicle;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);
		vehicle =new Vehicle();
		vehicle.setVehicleRegistrationNumber("MH15HQ7720");
		vehicle.setVehicleType(VehicleType.BIKE);
		vehicle.setColour("WHITE");
	}

	/**
	 * Test method for {@link com.ezest.controller.VehicleController#getAllParkingLots()}.
	 */
	@Test
	void testGetAllVehicles() {
		List<Vehicle> vehicles = new ArrayList<>();
		vehicles.add(vehicle);
		when(vehicleService.getVehicles()).thenReturn(vehicles);
		List<Vehicle> vehiclesList = vehicleController.getAllVehicles();
		assertTrue(vehicles.size() == vehiclesList.size() && vehicles.containsAll(vehiclesList) && vehiclesList.containsAll(vehicles));
	}

	/**
	 * Test method for {@link com.ezest.controller.VehicleController#createParkingLot(com.ezest.model.Vehicle)}.
	 */
	@Test
	void testCreateVehicle() {
		Vehicle notExistVehicle = null;
		when(vehicleService.findVehicleByPlate(vehicle.getVehicleRegistrationNumber())).thenReturn(Optional.ofNullable(notExistVehicle));
		ResponseEntity<Vehicle> responseEntity = vehicleController.createVehicle(vehicle);
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
	}
	
	/**
	 * Test method for {@link com.ezest.controller.VehicleController#createParkingLot(com.ezest.model.Vehicle)}.
	 */
	@Test
	void testCreateVehicleAlreadyExist() {
		Optional<Vehicle> existingVehicle  = Optional.of(vehicle);
		VehicleAlreadyExistException thrown = assertThrows(
				VehicleAlreadyExistException.class,
		           () -> {
		        	   when(vehicleService.findVehicleByPlate(vehicle.getVehicleRegistrationNumber())).thenReturn(existingVehicle);
		        	   vehicleController.createVehicle(vehicle);
		           }
		    );
		Assertions.assertEquals(new VehicleAlreadyExistException().getMessage(),thrown.getMessage());
	}

}

package com.ezest.service.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ezest.enumerators.VehicleType;
import com.ezest.model.Vehicle;
import com.ezest.repository.VehicleRepository;

class VehicleServiceImplTest {
	
	@InjectMocks
	VehicleServiceImpl vehicleServiceImpl; 
	
	@Mock
	VehicleRepository vehicleRepository;
	
	Vehicle vehicle;

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);
		vehicle =new Vehicle();
		vehicle.setVehicleRegistrationNumber("MH15HQ7720");
		vehicle.setVehicleType(VehicleType.BIKE);
		vehicle.setColour("WHITE");
	}

	@Test
	void testGetVehicles() {
		List<Vehicle> vehicles =new ArrayList<>();
		vehicles.add(vehicle);
		when(vehicleRepository.findAll()).thenReturn(vehicles);
		List<Vehicle> existingVehicles = vehicleServiceImpl.getVehicles();
		assertTrue(vehicles.size() == existingVehicles.size() && vehicles.containsAll(existingVehicles) && vehicles.containsAll(existingVehicles));
	}

	@Test
	void testCreateVehicle() {
		when(vehicleRepository.save(vehicle)).thenReturn(vehicle);
		Vehicle createVehicle = vehicleServiceImpl.createVehicle(vehicle);
		assertNotNull(createVehicle);
	}

	@Test
	void testFindVehicleByPlate() {
		when(vehicleRepository.getVehicleByVehicleRegistrationNumber(vehicle.getVehicleRegistrationNumber())).thenReturn(Optional.ofNullable(vehicle));
		Optional<Vehicle> findVehicleByPlate = vehicleServiceImpl.findVehicleByPlate(vehicle.getVehicleRegistrationNumber());
		assertTrue(findVehicleByPlate.isPresent());
	}

}

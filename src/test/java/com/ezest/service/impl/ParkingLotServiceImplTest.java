package com.ezest.service.impl;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ezest.enumerators.VehicleType;
import com.ezest.model.ParkingLot;
import com.ezest.repository.ParkingLotRepository;

class ParkingLotServiceImplTest {
	
	@InjectMocks
	ParkingLotServiceImpl parkingLotServiceImpl; 
	
	@Mock
	private ParkingLotRepository parkingLotRepository;
	
	ParkingLot parkingLot;

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);
		parkingLot = new ParkingLot();
		parkingLot.setName("Bike Lot");
		parkingLot.setLotVehicleType(VehicleType.BIKE);
		parkingLot.setLotSize(2L);
		parkingLot.setFloor("Ground Floor");
		parkingLot.setPrice(30L);
	}

	@Test
	@DisplayName("Create parking lot")
	void testCreateParkingLot() {
		when(parkingLotRepository.save(parkingLot)).thenReturn(parkingLot);
		ParkingLot createParkingLot = parkingLotServiceImpl.createParkingLot(parkingLot);
		assertNotNull(createParkingLot);
		assertEquals(parkingLot, createParkingLot);
	}

	@Test
	@DisplayName("Get all parking Lots")
	void testGetParkingLots() {
		List<ParkingLot> parkingLots=new ArrayList<>();	
		parkingLots.add(parkingLot);
		when(parkingLotRepository.findAll()).thenReturn(parkingLots);
		List<ParkingLot> getParkingLots = parkingLotServiceImpl.getParkingLots();
		assertAll("parking Lots",
		        () -> assertEquals(parkingLots.size(), getParkingLots.size()),
		        () -> assertTrue(parkingLots.containsAll(getParkingLots)),
		        () -> assertIterableEquals(parkingLots, getParkingLots));
	}

	@Test
	@DisplayName("Find parking lot by parking lot name")
	void testFindByName() {
		when(parkingLotRepository.findByName(parkingLot.getName())).thenReturn(Optional.ofNullable(parkingLot));
		Optional<ParkingLot> existingParkingLot = parkingLotServiceImpl.findByName(parkingLot.getName());
		assertTrue(existingParkingLot.isPresent());
	}

	@Test
	@DisplayName("Find all parking lots by vehicle type")
	void testFindAllByVehicleType() {
		List<ParkingLot> parkingLots=new ArrayList<>();
		parkingLots.add(parkingLot);
		when(parkingLotRepository.findByLotVehicleType(VehicleType.BIKE)).thenReturn(parkingLots);
		List<ParkingLot> getParkingLots = parkingLotServiceImpl.findAllByVehicleType(VehicleType.BIKE);
		assertAll("parking Lots by vehicle type",
		        () -> assertEquals(parkingLots.size(), getParkingLots.size()),
		        () -> assertTrue(parkingLots.containsAll(getParkingLots)),
		        () -> assertIterableEquals(parkingLots, getParkingLots));
	}

}

package com.ezest.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
	void testCreateParkingLot() {
		when(parkingLotRepository.save(parkingLot)).thenReturn(parkingLot);
		ParkingLot createParkingLot = parkingLotServiceImpl.createParkingLot(parkingLot);
		assertNotNull(createParkingLot);
		assertEquals(parkingLot, createParkingLot);
	}

	@Test
	void testGetParkingLots() {
		List<ParkingLot> parkingLots=new ArrayList<>();	
		parkingLots.add(parkingLot);
		when(parkingLotRepository.findAll()).thenReturn(parkingLots);
		List<ParkingLot> createParkingLot = parkingLotServiceImpl.getParkingLots();
		assertTrue(parkingLots.size() == createParkingLot.size() && parkingLots.containsAll(createParkingLot) && parkingLots.containsAll(createParkingLot));
	}

	@Test
	void testFindByName() {
		when(parkingLotRepository.findByName(parkingLot.getName())).thenReturn(Optional.ofNullable(parkingLot));
		Optional<ParkingLot> existingParkingLot = parkingLotServiceImpl.findByName(parkingLot.getName());
		assertTrue(existingParkingLot.isPresent());
	}

	@Test
	void testFindAllByVehicleType() {
		List<ParkingLot> parkingLots=new ArrayList<>();
		parkingLots.add(parkingLot);
		when(parkingLotRepository.findByLotVehicleType(VehicleType.BIKE)).thenReturn(parkingLots);
		List<ParkingLot> getParkingLots = parkingLotServiceImpl.findAllByVehicleType(VehicleType.BIKE);
		assertTrue(parkingLots.size() == getParkingLots.size() && parkingLots.containsAll(getParkingLots) && parkingLots.containsAll(getParkingLots));
	}

}

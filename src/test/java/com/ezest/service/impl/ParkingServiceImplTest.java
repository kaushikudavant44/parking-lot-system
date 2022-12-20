package com.ezest.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ezest.enumerators.ParkingStatus;
import com.ezest.enumerators.VehicleType;
import com.ezest.exceptions.ParkingLotNotFoundException;
import com.ezest.exceptions.VehicleAlreadyCheckedInException;
import com.ezest.model.Parking;
import com.ezest.model.ParkingLot;
import com.ezest.model.Vehicle;
import com.ezest.repository.ParkingLotRepository;
import com.ezest.repository.ParkingRepository;
import com.ezest.service.ParkingLotService;
import com.ezest.service.VehicleService;

class ParkingServiceImplTest {
	
	@InjectMocks
	ParkingServiceImpl parkingServiceImpl;
	
	@Mock
	ParkingRepository parkingRepository;
	
	@Mock
	ParkingLotRepository parkingLotRepository;
	
	@Mock
	ParkingLotService parkingLotService;
	
	@Mock
	VehicleService vehicleService;
	
	Parking parking;
	
	Vehicle vehicle;

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

	@Test
	void testGetParkingList() {
		List<Parking> parkings=new ArrayList<>();	
		parkings.add(parking);
		when(parkingRepository.findAll()).thenReturn(parkings);
		List<Parking> getParkingRecords = parkingServiceImpl.getParkingList();
		assertTrue(parkings.size() == getParkingRecords.size() && parkings.containsAll(getParkingRecords) && parkings.containsAll(getParkingRecords));

	}

	@Test
	void testCreateParkingRecord() {
		List<ParkingLot> parkingLots=new ArrayList<>();
		Parking parking1 = null;
		ParkingLot parkingLot = new ParkingLot();
		parkingLot.setName("Bike Lot");
		parkingLot.setLotVehicleType(VehicleType.BIKE);
		parkingLot.setLotSize(2L);
		parkingLot.setFloor("Ground Floor");
		parkingLot.setPrice(30L);
		parkingLots.add(parkingLot);
		when(vehicleService.findVehicleByPlate(any(String.class))).thenReturn(Optional.ofNullable(vehicle));
	//	when(vehicleService.createVehicle(vehicle)).thenReturn(vehicle);
		when(parkingRepository.findByVehicleRegistrationNumberAndParkingStatus(
				vehicle.getVehicleRegistrationNumber(), ParkingStatus.RESERVE)).thenReturn(Optional.ofNullable(parking1));
		assertFalse(Optional.ofNullable(parking1).isPresent());
		when(parkingLotService.findAllByVehicleType(VehicleType.BIKE)).thenReturn(parkingLots);
		doReturn(parking).when(parkingRepository).save(any(Parking.class));
		parkingLot.setLotSize(parkingLot.getLotSize() - 1);
		when(parkingLotRepository.save(parkingLot)).thenReturn(parkingLot);
		Parking createParkingRecords = parkingServiceImpl.createParkingRecord(vehicle);
		assertNotNull(createParkingRecords);
	}
	
	@Test
	void testCreateParkingRecordNotFound() {
		List<ParkingLot> parkingLots=null;
		Parking parking1 = null;
		ParkingLot parkingLot =null;		
		when(vehicleService.findVehicleByPlate(any(String.class))).thenReturn(Optional.ofNullable(vehicle));
		when(parkingRepository.findByVehicleRegistrationNumberAndParkingStatus(
				vehicle.getVehicleRegistrationNumber(), ParkingStatus.RESERVE)).thenReturn(Optional.ofNullable(parking1));
		assertFalse(Optional.ofNullable(parking1).isPresent());
		when(parkingLotService.findAllByVehicleType(VehicleType.BIKE)).thenReturn(parkingLots);
		doReturn(parking).when(parkingRepository).save(any(Parking.class));
		when(parkingLotRepository.save(parkingLot)).thenReturn(parkingLot);
		
		ParkingLotNotFoundException thrown = Assertions.assertThrows(ParkingLotNotFoundException.class, () -> {
					parkingServiceImpl.createParkingRecord(vehicle);
		}, "ParkingLotNotFoundException was expected");
		
		Assertions.assertEquals(new ParkingLotNotFoundException().getMessage(),thrown.getMessage());
	}
	
	

	@Test
	void testUpdateParkingRecord() {
		ParkingLot parkingLot = new ParkingLot();
		parkingLot.setName("Bike Lot");
		parkingLot.setLotVehicleType(VehicleType.BIKE);
		parkingLot.setLotSize(2L);
		parkingLot.setFloor("Ground Floor");
		parkingLot.setPrice(30L);
		when(parkingRepository.getParkingByParkingId(parking.getParkingId())).thenReturn(Optional.ofNullable(parking));
		assertFalse(parking.getEndDate()!=null);
		when(parkingLotService.findByName(parking.getParkingLot())).thenReturn(Optional.ofNullable(parkingLot));
		assertTrue(Optional.ofNullable(parkingLot).isPresent());
		when(parkingRepository.save(parking)).thenReturn(parking);
		when(parkingLotRepository.save(parkingLot)).thenReturn(parkingLot);
		Parking updateParkingRecord = parkingServiceImpl.updateParkingRecord(parking.getParkingId());
		assertNotNull(updateParkingRecord);
	}

	@Test
	void testFindSuitableLot() {
		List<ParkingLot> parkingLots=new ArrayList<>();
		ParkingLot parkingLot = new ParkingLot();
		parkingLot.setName("Bike Lot");
		parkingLot.setLotVehicleType(VehicleType.BIKE);
		parkingLot.setLotSize(2L);
		parkingLot.setFloor("Ground Floor");
		parkingLot.setPrice(30L);
		parkingLots.add(parkingLot);
		when(parkingLotService.findAllByVehicleType(VehicleType.BIKE)).thenReturn(parkingLots);
		assertTrue(parkingLots.size()!=0);
		ParkingLot suitableLot = parkingServiceImpl.findSuitableLot(VehicleType.BIKE);
		assertNotNull(suitableLot);
	}

	@Test
	void testCalculatePriceForMoreThanTwoHoursVehicle() {
		double price = 30;
		LocalDateTime checkIn = LocalDateTime.now();
		LocalDateTime checkOut = LocalDateTime.now().plusHours(10);
		int hours = (int) ChronoUnit.HOURS.between(checkIn, checkOut);
		double calculatePrice = parkingServiceImpl.calculatePrice(checkIn, checkOut, price);
		assertEquals(price + ((hours - 2) * 10), calculatePrice);
	}

	@Test
	void testCheckIfVehicleExists() {
		List<ParkingLot> parkingLots=new ArrayList<>();
		Parking parking1 = null;
		ParkingLot parkingLot = new ParkingLot();
		parkingLot.setName("BIKE Lot");
		parkingLot.setLotVehicleType(VehicleType.BIKE);
		parkingLot.setLotSize(2L);
		parkingLot.setFloor("Ground Floor");
		parkingLot.setPrice(30L);
		parkingLots.add(parkingLot);
		Vehicle newVehicle=null;
		when(vehicleService.findVehicleByPlate(any(String.class))).thenReturn(Optional.ofNullable(newVehicle));
		when(vehicleService.createVehicle(vehicle)).thenReturn(vehicle);
		when(parkingRepository.findByVehicleRegistrationNumberAndParkingStatus(
				vehicle.getVehicleRegistrationNumber(), ParkingStatus.RESERVE)).thenReturn(Optional.ofNullable(parking1));
		assertFalse(Optional.ofNullable(parking1).isPresent());
		when(parkingLotService.findAllByVehicleType(VehicleType.BIKE)).thenReturn(parkingLots);
		doReturn(parking).when(parkingRepository).save(any(Parking.class));
		parkingLot.setLotSize(parkingLot.getLotSize() - 1);
		when(parkingLotRepository.save(parkingLot)).thenReturn(parkingLot);
		Parking createParkingRecords = parkingServiceImpl.createParkingRecord(vehicle);
		assertNotNull(createParkingRecords);
	}

	@Test
	void testCheckIfVehicleAlreadyParked() {
		List<ParkingLot> parkingLots=null;
		ParkingLot parkingLot =null;		
		when(vehicleService.findVehicleByPlate(any(String.class))).thenReturn(Optional.ofNullable(vehicle));
		when(parkingRepository.findByVehicleRegistrationNumberAndParkingStatus(
				vehicle.getVehicleRegistrationNumber(), ParkingStatus.RESERVE)).thenReturn(Optional.ofNullable(parking));
		assertTrue(Optional.ofNullable(parking).isPresent());
		when(parkingLotService.findAllByVehicleType(VehicleType.BIKE)).thenReturn(parkingLots);
		doReturn(parking).when(parkingRepository).save(any(Parking.class));
		when(parkingLotRepository.save(parkingLot)).thenReturn(parkingLot);
		
		VehicleAlreadyCheckedInException thrown = Assertions.assertThrows(VehicleAlreadyCheckedInException.class, () -> {
					parkingServiceImpl.createParkingRecord(vehicle);
		}, "VehicleAlreadyCheckedInException was expected");
		
		Assertions.assertEquals(new VehicleAlreadyCheckedInException().getMessage(),thrown.getMessage());
	}

	@Test
	void testGetCurrentParkedVehicleByLicencePlate() {
		when(parkingRepository.findByVehicleRegistrationNumberAndParkingStatus(
				vehicle.getVehicleRegistrationNumber(), ParkingStatus.RESERVE)).thenReturn(Optional.ofNullable(parking));
		Parking currentParkedVehicleByLicencePlate = parkingServiceImpl.getCurrentParkedVehicleByLicencePlate(vehicle.getVehicleRegistrationNumber());
		assertNotNull(currentParkedVehicleByLicencePlate);
	}

}

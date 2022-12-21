package com.ezest.service.impl;

import static org.junit.jupiter.api.Assertions.assertAll;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ezest.enumerators.ParkingStatus;
import com.ezest.enumerators.VehicleType;
import com.ezest.exceptions.ParkingLotNotFoundException;
import com.ezest.exceptions.ParkingRecordNotFoundException;
import com.ezest.exceptions.VehicleAlreadyCheckedInException;
import com.ezest.exceptions.VehicleAlreadyCheckedOutException;
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
	@DisplayName("Getting all parking records")
	void testGetParkingList() {
		List<Parking> parkings=new ArrayList<>();	
		parkings.add(parking);
		when(parkingRepository.findAll()).thenReturn(parkings);
		List<Parking> getParkingRecords = parkingServiceImpl.getParkingList();
		assertAll("parking records",
		        () -> assertEquals(parkings.size(), getParkingRecords.size()),
		        () -> assertTrue(parkings.containsAll(getParkingRecords)));
	}

	@Test
	@DisplayName("Check in new vehicle in parking or parked vehicle")
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
	@DisplayName("Parking full or parking lot not found")
	void testCreateParkingLotNotFound() {
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
		
		ParkingLotNotFoundException thrown = Assertions.assertThrows(ParkingLotNotFoundException.class, () -> 
					parkingServiceImpl.createParkingRecord(vehicle)
		, "ParkingLotNotFoundException was expected");
		
		Assertions.assertEquals(new ParkingLotNotFoundException().getMessage(),thrown.getMessage());
	}
	
	

	@Test
	@DisplayName("Checkout vehicle & free parking lot")
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
	@DisplayName("Throw error if trying to trying to checkout vehicle if vehicle already checkout")
	void testCheckIfVehicleAlreadyCheckout() {
		ParkingLot parkingLot = null;
		parking.setEndDate(LocalDateTime.now().plusHours(3));
		when(parkingRepository.getParkingByParkingId(parking.getParkingId())).thenReturn(Optional.ofNullable(parking));
		assertTrue(parking.getEndDate()!=null);
		when(parkingLotService.findByName(parking.getParkingLot())).thenReturn(Optional.ofNullable(parkingLot));
		when(parkingRepository.save(parking)).thenReturn(parking);
		when(parkingLotRepository.save(parkingLot)).thenReturn(parkingLot);
		
		VehicleAlreadyCheckedOutException thrown = Assertions.assertThrows(VehicleAlreadyCheckedOutException.class, () -> {
			parkingServiceImpl.updateParkingRecord(parking.getParkingId());
		}, "VehicleAlreadyCheckedOutException was expected");
		
		Assertions.assertEquals(new VehicleAlreadyCheckedOutException().getMessage(),thrown.getMessage());
	}
	
	@Test
	@DisplayName("Throw error if parking lot not available")
	void testCheckParkingLotNotFound() {
		ParkingLot parkingLot = null;
		when(parkingRepository.getParkingByParkingId(parking.getParkingId())).thenReturn(Optional.ofNullable(parking));
		assertFalse(parking.getEndDate()!=null);
		when(parkingLotService.findByName(parking.getParkingLot())).thenReturn(Optional.ofNullable(parkingLot));
		when(parkingRepository.save(parking)).thenReturn(parking);
		when(parkingLotRepository.save(parkingLot)).thenReturn(parkingLot);
		
		ParkingLotNotFoundException thrown = Assertions.assertThrows(ParkingLotNotFoundException.class, () -> {
			parkingServiceImpl.updateParkingRecord(parking.getParkingId());
		}, "ParkingLotNotFoundException was expected");
		
		Assertions.assertEquals(new ParkingLotNotFoundException().getMessage(),thrown.getMessage());
	}
	
	@Test
	@DisplayName("Thrown error if parking record not found")
	void testCheckParkingRecordNotFoundException() {
		ParkingLot parkingLot = null;
		when(parkingRepository.getParkingByParkingId(parking.getParkingId())).thenReturn(Optional.ofNullable(null));
		assertFalse(parking.getEndDate()!=null);
		when(parkingLotService.findByName(parking.getParkingLot())).thenReturn(Optional.ofNullable(parkingLot));
		when(parkingRepository.save(parking)).thenReturn(parking);
		when(parkingLotRepository.save(parkingLot)).thenReturn(parkingLot);
		
		ParkingRecordNotFoundException thrown = Assertions.assertThrows(ParkingRecordNotFoundException.class, () -> {
			parkingServiceImpl.updateParkingRecord(parking.getParkingId());
		}, "ParkingRecordNotFoundException was expected");
		
		Assertions.assertEquals(new ParkingRecordNotFoundException().getMessage(),thrown.getMessage());
	}
	

	@Test
	@DisplayName("Find slot to park vehicle")
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
	@DisplayName("Calculate vehicle parking price if vehicle is parked more than tow hors")
	void testCalculatePriceForMoreThanTwoHoursVehicle() {
		double price = 30;
		LocalDateTime checkIn = LocalDateTime.now();
		LocalDateTime checkOut = LocalDateTime.now().plusHours(10);
		int hours = (int) ChronoUnit.HOURS.between(checkIn, checkOut);
		double calculatePrice = parkingServiceImpl.calculatePrice(checkIn, checkOut, price);
		assertEquals(price + ((hours - 2) * 10), calculatePrice);
	}

	@Test
	@DisplayName("Check vehicle is existed")
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
	@DisplayName("Throw error if vehicle already parked")
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
	@DisplayName("Find current parked vehicle by registration number")
	void testGetCurrentParkedVehicleByLicencePlate() {
		when(parkingRepository.findByVehicleRegistrationNumberAndParkingStatus(
				vehicle.getVehicleRegistrationNumber(), ParkingStatus.RESERVE)).thenReturn(Optional.ofNullable(parking));
		Parking currentParkedVehicleByLicencePlate = parkingServiceImpl.getCurrentParkedVehicleByLicencePlate(vehicle.getVehicleRegistrationNumber());
		assertNotNull(currentParkedVehicleByLicencePlate);
	}

}

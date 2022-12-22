/**
 * 
 */
package com.ezest.integration.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import com.ezest.enumerators.ParkingStatus;
import com.ezest.enumerators.VehicleType;
import com.ezest.model.Parking;
import com.ezest.model.ParkingLot;
import com.ezest.model.Vehicle;
import com.ezest.repository.ParkingLotRepository;
import com.ezest.repository.ParkingRepository;
import com.ezest.repository.VehicleRepository;
import com.ezest.service.ParkingService;

/**
 * @author ADMIN
 *
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-integration.properties")
class ParkingControllerIntegrationTest {

	@LocalServerPort
	private int port;

	private String baseUrl = "http://localhost";

	private static RestTemplate restTemplate;

	Parking parking;

	Vehicle vehicle;
	
	@Autowired
	ParkingRepository parkingRepository; 
	
	@Autowired
	VehicleRepository vehicleRepository; 
	
	@Autowired
	ParkingService parkingService;
	
	@Autowired
	ParkingLotRepository parkingLotRepository;

	@BeforeAll
	public static void init() {
		restTemplate = new RestTemplate();
	}

	@BeforeEach
	void setUp() throws Exception {
		
		MockitoAnnotations.openMocks(this);
		baseUrl = baseUrl + ":" +port + "/api/v1/parking";
		
		parking = new Parking();
		parking.setParkingLot("BIKE lot");
		parking.setVehicleRegistrationNumber("MH15HQ7720");
		parking.setParkingStatus(ParkingStatus.RESERVE);
		parking.setPrice(30);
		parking.setStartDate(LocalDateTime.now());
		parking.setEndDate(null);
		vehicle = new Vehicle();
		vehicle.setVehicleRegistrationNumber("MH15HQ7720");
		vehicle.setVehicleType(VehicleType.BIKE);
		vehicle.setColour("WHITE");
		parking =parkingRepository.save(parking);
		vehicle = vehicleRepository.save(vehicle);
	}
	
	@AfterEach
	public void afterSetup() {
		parkingRepository.deleteAll();
		vehicleRepository.deleteAll();
		parkingLotRepository.deleteAll();
	}

	/**
	 * Test method for
	 * {@link com.ezest.controller.ParkingController#getAllParkingRecords()}.
	 */
	@Test
	@DisplayName("GET /api/v1/parking parking record")
	void testGetAllParkingRecords() {
		List<Parking> list = restTemplate.getForObject(baseUrl, List.class);	
		assertThat(list).hasSize(1);
	}

	/**
	 * Test method for
	 * {@link com.ezest.controller.ParkingController#getCurrentParkingRecords()}.
	 */
	@Test
	@DisplayName("GET /api/v1/parking/current current parking record")
	void testGetCurrentParkingRecords() {
		List<Parking> list = restTemplate.getForObject(baseUrl+"/current", List.class);	
		assertThat(list).hasSize(1);
	}

	/**
	 * Test method for
	 * {@link com.ezest.controller.ParkingController#getCurrentParkingWithLicencePlate(java.lang.String)}.
	 */
	@Test
	@DisplayName("GET /api/v1/parking/get/MH15HQ7708 current parked parking record by vehicle number")
	void testGetCurrentParkingWithLicencePlate() {
		Parking parkingRecord = restTemplate.getForObject(baseUrl+"/get/"+parking.getVehicleRegistrationNumber(), Parking.class);	
		assertNotNull(parkingRecord);
		assertThat(parkingRecord.getVehicleRegistrationNumber()).isEqualTo(parking.getVehicleRegistrationNumber());
	}

	/**
	 * Test method for
	 * {@link com.ezest.controller.ParkingController#checkInToParkingLot(com.ezest.model.Vehicle)}.
	 *
	 * {@link com.ezest.controller.ParkingController#checkOutFromParkingLot(int)}.
	 */
	@Test
	@DisplayName("POST /api/v1/parking/checkin Parked vehicle & PUT /api/v1/parking/checkout Unparked vehicle" )
	void testCheckInToParkingLotAndCheckoutFromParkingLot() {
		ParkingLot parkingLot = new ParkingLot();
		parkingLot.setName("Bike Lot B");
		parkingLot.setLotVehicleType(VehicleType.BIKE);
		parkingLot.setLotSize(2L);
		parkingLot.setFloor("Ground Floor");
		parkingLot.setPrice(30L);
		parkingLot = parkingLotRepository.save(parkingLot);
		Vehicle vehicle = new Vehicle();
		vehicle.setVehicleRegistrationNumber("MH15HQ7710");
		vehicle.setVehicleType(VehicleType.BIKE);
		vehicle.setColour("WHITE");
		ResponseEntity<Parking> checkInToParkingLot = restTemplate.postForEntity(baseUrl+"/checkin", vehicle, Parking.class);
		assertNotNull(checkInToParkingLot);
		assertThat(checkInToParkingLot.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		HttpEntity<Parking> requestEntity = new HttpEntity<Parking>(parking);
		ResponseEntity<Parking> checkOutFromParkingLot = restTemplate.exchange(baseUrl+"/checkout/"+checkInToParkingLot.getBody().getParkingId(),HttpMethod.PUT,requestEntity,Parking.class);
		assertNotNull(checkOutFromParkingLot);
		assertThat(checkOutFromParkingLot.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

}

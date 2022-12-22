/**
 * 
 */
package com.ezest.integration.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import com.ezest.enumerators.VehicleType;
import com.ezest.model.ParkingLot;
import com.ezest.repository.ParkingLotRepository;

/**
 * @author ADMIN
 *
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-integration.properties")
class ParkingLotControllerIntegrationTest {
	
	@LocalServerPort
	private int port;

	private String baseUrl = "http://localhost";

	private static RestTemplate restTemplate;
	
	ParkingLot parkingLot;
	
	@Autowired
	ParkingLotRepository parkingLotRepository;
	
	@BeforeAll
	public static void init() {
		restTemplate = new RestTemplate();
	}

	@BeforeEach
	void setUp() throws Exception {
		
		MockitoAnnotations.openMocks(this);
		baseUrl = baseUrl + ":" +port + "/api/v1/parking_lot";
		parkingLot = new ParkingLot();
		parkingLot.setName("Bike Lot");
		parkingLot.setLotVehicleType(VehicleType.BIKE);
		parkingLot.setLotSize(2L);
		parkingLot.setFloor("Ground Floor");
		parkingLot.setPrice(30L);
		parkingLot = parkingLotRepository.save(parkingLot);
	}
	
	@AfterEach
	public void afterSetup() {
		parkingLotRepository.deleteAll();
	}

	/**
	 * Test method for {@link com.ezest.controller.ParkingLotController#createParkingLot(com.ezest.model.ParkingLot)}.
	 */
	@Test
	@DisplayName("POST /api/v1/parking_lot/create create new Parking lot")
	void testCreateParkingLot() {
		ParkingLot parkingLot = new ParkingLot();
		parkingLot.setName("Bike Lot A");
		parkingLot.setLotVehicleType(VehicleType.BIKE);
		parkingLot.setLotSize(2L);
		parkingLot.setFloor("Ground Floor");
		parkingLot.setPrice(30L);
		ResponseEntity<ParkingLot> createdParkingLot = restTemplate.postForEntity(baseUrl+"/create", parkingLot, ParkingLot.class);
		assertNotNull(createdParkingLot);
		assertThat(createdParkingLot.getStatusCode()).isEqualTo(HttpStatus.CREATED);
	}

	/**
	 * Test method for {@link com.ezest.controller.ParkingLotController#getAllParkingLots()}.
	 */
	@Test
	@DisplayName("GET /api/v1/parking_lot get all Parking lot")
	void testGetAllParkingLots() {
		List<ParkingLot> list = restTemplate.getForObject(baseUrl, List.class);	
		assertThat(list).hasSize(1);
	}

}

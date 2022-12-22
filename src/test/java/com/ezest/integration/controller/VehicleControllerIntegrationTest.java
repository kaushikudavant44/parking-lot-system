/**
 * 
 */
package com.ezest.integration.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

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
import com.ezest.model.Vehicle;
import com.ezest.repository.VehicleRepository;

/**
 * @author ADMIN
 *
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-integration.properties")
class VehicleControllerIntegrationTest {
	
	@LocalServerPort
	private int port;

	private String baseUrl = "http://localhost";

	private static RestTemplate restTemplate;
	
	@Autowired
	VehicleRepository vehicleRepository;
	
	Vehicle vehicle;
	
	@BeforeAll
	public static void init() {
		restTemplate = new RestTemplate();
	}

	@BeforeEach
	void setUp() throws Exception {
		
		MockitoAnnotations.openMocks(this);
		baseUrl = baseUrl + ":" +port + "/api/v1/vehicles";
		MockitoAnnotations.openMocks(this);
		vehicle =new Vehicle();
		vehicle.setVehicleRegistrationNumber("MH15HQ7720");
		vehicle.setVehicleType(VehicleType.BIKE);
		vehicle.setColour("WHITE");
		vehicleRepository.save(vehicle);
	}
	
	@AfterEach
	public void afterSetup() {
		vehicleRepository.deleteAll();
	}

	/**
	 * Test method for {@link com.ezest.controller.VehicleController#getAllVehicles()}.
	 */
	@Test
	@DisplayName("GET /api/v1/vehicles get all vehicles")
	void testGetAllVehicles() {
		List<Vehicle> list = restTemplate.getForObject(baseUrl, List.class);	
		assertThat(list).hasSize(1);
	}

	/**
	 * Test method for {@link com.ezest.controller.VehicleController#createVehicle(com.ezest.model.Vehicle)}.
	 */
	@Test
	@DisplayName("POST /api/v1/vehicles register new vehicle")
	void testCreateVehicle() {
		Vehicle vehicle =new Vehicle();
		vehicle.setVehicleRegistrationNumber("MH15HQ7750");
		vehicle.setVehicleType(VehicleType.BIKE);
		vehicle.setColour("WHITE");
		ResponseEntity<Vehicle> newVehicle = restTemplate.postForEntity(baseUrl+"/create", vehicle, Vehicle.class);
		assertNotNull(newVehicle);
		assertThat(newVehicle.getStatusCode()).isEqualTo(HttpStatus.CREATED);
	}

}

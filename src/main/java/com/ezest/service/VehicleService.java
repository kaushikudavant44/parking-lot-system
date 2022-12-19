/**
 * 
 */
package com.ezest.service;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import com.ezest.model.Vehicle;

/**
 * @author ADMIN
 *
 */
public interface VehicleService {

	List<Vehicle> getVehicles();

	Vehicle createVehicle(@Valid Vehicle vehicle);
	
	Optional<Vehicle> findVehicleByPlate(String licencePlate);
	   
}

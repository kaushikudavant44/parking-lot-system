/**
 * 
 */
package com.ezest.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.ezest.model.Vehicle;
import com.ezest.repository.VehicleRepository;
import com.ezest.service.VehicleService;

/**
 * @author ADMIN
 *
 */
@Service
public class VehicleServiceImpl implements VehicleService {

	@Autowired
	private VehicleRepository vehicleRepository;

	public List<Vehicle> getVehicles() {
		return vehicleRepository.findAll();
	}

	public Vehicle createVehicle(@RequestBody Vehicle vehicle) {
		return vehicleRepository.save(vehicle);
	}

	public Optional<Vehicle> findVehicleByPlate(String vehicleRegistrationNumber) {
		return vehicleRepository.getVehicleByVehicleRegistrationNumber(vehicleRegistrationNumber);
	}

}

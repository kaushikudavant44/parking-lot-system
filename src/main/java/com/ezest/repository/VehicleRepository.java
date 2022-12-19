/**
 * 
 */
package com.ezest.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ezest.model.Vehicle;

/**
 * @author ADMIN
 *
 */
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

	Optional<Vehicle> getVehicleByVehicleRegistrationNumber(String vehicleRegistrationNumber);

}

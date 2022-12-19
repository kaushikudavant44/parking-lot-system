/**
 * 
 */
package com.ezest.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ezest.enumerators.ParkingStatus;
import com.ezest.model.Parking;

/**
 * @author ADMIN
 *
 */
public interface ParkingRepository extends JpaRepository<Parking, Long> {
	
	Optional<Parking> getParkingByParkingId(int parkingId);

	Optional<Parking> findByVehicleRegistrationNumberAndParkingStatus(String vehicleRegistrationNumber, ParkingStatus reserve);

}

/**
 * 
 */
package com.ezest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ezest.enumerators.VehicleType;
import com.ezest.model.ParkingLot;

/**
 * @author ADMIN
 *
 */
public interface ParkingLotRepository extends JpaRepository<ParkingLot, Long>{

	List<ParkingLot> findByLotVehicleType(VehicleType lotVehicleType);

	Optional<ParkingLot> findByName(String parkingLot);

}

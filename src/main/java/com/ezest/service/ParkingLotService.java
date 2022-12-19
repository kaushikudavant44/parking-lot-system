/**
 * 
 */
package com.ezest.service;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import com.ezest.enumerators.VehicleType;
import com.ezest.model.ParkingLot;

/**
 * @author ADMIN
 *
 */
public interface ParkingLotService {

	ParkingLot createParkingLot(@Valid ParkingLot parkingLot);

	List<ParkingLot> getParkingLots();

	Optional<ParkingLot> findByName(String parkingLot);

	List<ParkingLot> findAllByVehicleType(VehicleType lotVehicleType);

}

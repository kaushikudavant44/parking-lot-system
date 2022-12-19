/**
 * 
 */
package com.ezest.service.impl;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.ezest.enumerators.VehicleType;
import com.ezest.model.ParkingLot;
import com.ezest.repository.ParkingLotRepository;
import com.ezest.service.ParkingLotService;

/**
 * @author ADMIN
 *
 */
@Service
public class ParkingLotServiceImpl implements ParkingLotService{
	
	private static Logger logger = LoggerFactory.getLogger(ParkingLotServiceImpl.class);
	
	@Autowired
    private ParkingLotRepository parkingLotRepository;

	@Override
	 public ParkingLot createParkingLot(@RequestBody ParkingLot parkingLot) {
        logger.debug("Parking lot created " + parkingLot.getName());
        return parkingLotRepository.save(parkingLot);
    }

	@Override
	public List<ParkingLot> getParkingLots() {
		return parkingLotRepository.findAll();
	}

	@Override
	public Optional<ParkingLot> findByName(String parkingLot) {
		Optional<ParkingLot> existingParkingLot = parkingLotRepository.findByName(parkingLot);
		
		return existingParkingLot; 
		
	}

	@Override
	public List<ParkingLot> findAllByVehicleType(VehicleType lotVehicleType) {
		return parkingLotRepository.findByLotVehicleType(lotVehicleType);
	}
}

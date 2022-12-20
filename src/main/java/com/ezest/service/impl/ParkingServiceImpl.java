/**
 * 
 */
package com.ezest.service.impl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ezest.enumerators.ParkingStatus;
import com.ezest.enumerators.VehicleType;
import com.ezest.exceptions.ParkingLotNotFoundException;
import com.ezest.exceptions.ParkingRecordNotFoundException;
import com.ezest.exceptions.VehicleAlreadyCheckedInException;
import com.ezest.exceptions.VehicleAlreadyCheckedOutException;
import com.ezest.model.Parking;
import com.ezest.model.ParkingLot;
import com.ezest.model.Vehicle;
import com.ezest.repository.ParkingLotRepository;
import com.ezest.repository.ParkingRepository;
import com.ezest.service.ParkingLotService;
import com.ezest.service.ParkingService;
import com.ezest.service.VehicleService;

/**
 * @author ADMIN
 *
 */
@Service
public class ParkingServiceImpl implements ParkingService {

	Logger logger = LoggerFactory.getLogger(ParkingService.class);

	@Autowired
	private ParkingRepository parkingRepository;

	@Autowired
	private VehicleService vehicleService;

	@Autowired
	private ParkingLotService parkingLotService;

	@Autowired
	private ParkingLotRepository parkingLotRepository;

	public List<Parking> getParkingList() {
		return parkingRepository.findAll();
	}

	@Transactional
	public Parking createParkingRecord(Vehicle comingVehicle) {
		Vehicle vehicle = checkIfVehicleExists(comingVehicle);
		checkIfVehicleAlreadyParked(vehicle);
		ParkingLot parkingLot = findSuitableLot(vehicle.getVehicleType());

		if (parkingLot != null) {
			parkingLot.setLotSize(parkingLot.getLotSize() - 1);
			Parking parking = parkingRepository.save(new Parking(parkingLot.getName(), vehicle.getVehicleRegistrationNumber(),
					java.time.LocalDateTime.now(), null, ParkingStatus.RESERVE, 0));
			parkingLotRepository.save(parkingLot);
			return parking;

		} else {
			throw new ParkingLotNotFoundException();
		}

	}

	@Transactional
	public Parking updateParkingRecord(int parkingId) {
		LocalDateTime checkOutDate = java.time.LocalDateTime.now();
		double unitPrice = 0;

		logger.debug("Checking existing parking records, if not found throw an exception");
		Parking existingParking = parkingRepository.getParkingByParkingId(parkingId)
				.orElseThrow(ParkingRecordNotFoundException::new);

		logger.debug("No record exists checking end date.");
		if (existingParking.getEndDate() != null)
			throw new VehicleAlreadyCheckedOutException();

		logger.debug("Find price for the lot");
		Optional<ParkingLot> parkingLot = parkingLotService.findByName(existingParking.getParkingLot());

		logger.debug("Calculating price for the lot");
		if (parkingLot.isPresent()) {
			unitPrice = parkingLot.get().getPrice();
		} else {
			throw new ParkingLotNotFoundException();
		}

		logger.debug("Setting end date and price for record, setting lot as not empty");
		existingParking.setEndDate(checkOutDate);
		existingParking.setPrice(calculatePrice(existingParking.getStartDate(), checkOutDate, unitPrice));
		existingParking.setParkingStatus(ParkingStatus.EMPTY);
		Parking parkingVehicleCheckout = parkingRepository.save(existingParking);
		parkingLot.get().setLotSize(parkingLot.get().getLotSize() + 1);
		parkingLotRepository.save(parkingLot.get());
		return parkingVehicleCheckout;

	}

	public ParkingLot findSuitableLot(VehicleType lotVehicleType) {

		List<ParkingLot> parkingLotList = parkingLotService.findAllByVehicleType(lotVehicleType);
		logger.debug("Getting suitable lot lists with looking height first.");

		if (parkingLotList != null) {
			for (ParkingLot parkingLot : parkingLotList) {

				if (parkingLot.getLotSize() != 0) {
					logger.debug("Found a suitable lot " + parkingLot.getName());
					return parkingLot;
				}

			}
		}
		logger.debug("Could not found a suitable lot.");
		throw new ParkingLotNotFoundException();
	}

	public double calculatePrice(LocalDateTime checkInDate, LocalDateTime checkOutDate, double price) {
		int hours = (int) ChronoUnit.HOURS.between(checkInDate, checkOutDate);
		logger.debug("Price is calculated for " + hours);
		if (hours <= 2) {
			return price;
		}
		return price + ((hours - 2) * 10);
	}

	public Vehicle checkIfVehicleExists(Vehicle vehicle) {
		Optional<Vehicle> comingVehicle = vehicleService.findVehicleByPlate(vehicle.getVehicleRegistrationNumber());

		if (comingVehicle.isPresent()) {
			vehicle = comingVehicle.get();
			logger.debug("Vehicle " + vehicle.getId() + "exists.");
			return vehicle;
		} else {
			logger.debug("Vehicle is not exists. Creating a new vehicle.");
			return vehicleService.createVehicle(vehicle);
		}
	}

	public void checkIfVehicleAlreadyParked(Vehicle vehicle) {
		// Check if vehicle already in the park
		Optional<Parking> oldPark = parkingRepository.findByVehicleRegistrationNumberAndParkingStatus(
				vehicle.getVehicleRegistrationNumber(), ParkingStatus.RESERVE);

		if (oldPark.isPresent()) {
			logger.debug("Vehicle already checked-in");
			throw new VehicleAlreadyCheckedInException();
		}
	}

	@Override
	public Parking getCurrentParkedVehicleByLicencePlate(String licencePlate) {
		return parkingRepository.findByVehicleRegistrationNumberAndParkingStatus(licencePlate, ParkingStatus.RESERVE)
				.get();
	}

}

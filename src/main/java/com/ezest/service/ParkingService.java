package com.ezest.service;

import java.util.List;

import javax.validation.Valid;

import com.ezest.model.Parking;
import com.ezest.model.Vehicle;

public interface ParkingService {

	List<Parking> getParkingList();

	Parking createParkingRecord(@Valid Vehicle vehicle);

	Parking updateParkingRecord(int id);

	Parking getCurrentParkedVehicleByLicencePlate(String licencePlate);

}

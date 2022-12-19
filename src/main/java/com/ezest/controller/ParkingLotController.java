package com.ezest.controller;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ezest.exceptions.ParkingLotNameAlreadyExistException;
import com.ezest.model.ParkingLot;
import com.ezest.service.ParkingLotService;


@RestController
@RequestMapping("/api/v1/parking_lot")
public class ParkingLotController {

	@Autowired
	private ParkingLotService parkingLotService;

	@PostMapping("/create")
	public ResponseEntity<ParkingLot> createParkingLot(@RequestBody @Valid ParkingLot parkingLot) {
		
		Optional<ParkingLot> existingParkingLot = parkingLotService.findByName(parkingLot.getName());
		if(existingParkingLot.isPresent()) {
			throw new ParkingLotNameAlreadyExistException();
		}
		
		return new ResponseEntity<>(parkingLotService.createParkingLot(parkingLot), HttpStatus.CREATED);
	}
	
	@GetMapping
    public ResponseEntity<List<ParkingLot>> getAllParkingLots() {
		return new ResponseEntity<>(parkingLotService.getParkingLots(), HttpStatus.OK);
    }

}

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

import com.ezest.exceptions.VehicleAlreadyExistException;
import com.ezest.model.Vehicle;
import com.ezest.service.VehicleService;

@RestController
@RequestMapping("/api/v1/vehicles")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @GetMapping
    public List<Vehicle> getAllVehicles() {
        return vehicleService.getVehicles();
    }


    @PostMapping("/create")
    public ResponseEntity<Vehicle> createVehicle(@RequestBody @Valid Vehicle vehicle) {
    	
    	Optional<Vehicle> existingVehicle = vehicleService.findVehicleByPlate(vehicle.getVehicleRegistrationNumber());
    	
    	if(existingVehicle.isPresent()) {
    		throw new VehicleAlreadyExistException();
    	}  	
        return new ResponseEntity<>(vehicleService.createVehicle(vehicle), HttpStatus.CREATED);
    }
}

package com.ezest.controller;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ezest.exceptions.VehicleNotFoundException;
import com.ezest.model.Parking;
import com.ezest.model.Vehicle;
import com.ezest.service.ParkingService;

@RestController
@RequestMapping("/api/v1/parking")
public class ParkingController
{

    @Autowired
    private ParkingService parkingService;

    @GetMapping
    public List<Parking> getAllParkingRecords()
    {
        return parkingService.getParkingList();
    }

    @GetMapping("/current")
    public List<Parking> getCurrentParkingRecords()
    {
        List<Parking> result = new ArrayList<>();
        parkingService.getParkingList().forEach(result::add);
        result=result.stream().filter(parking -> parking.getEndDate()==null).collect(Collectors.toList());
        return result;
    }

    @GetMapping("/get/{licence_plate}")
    public Parking getCurrentParkingWithLicencePlate(@PathVariable(value = "licence_plate") String licencePlate)
    {
        
        Parking currentParkedVehicle = parkingService.getCurrentParkedVehicleByLicencePlate(licencePlate);

        if(Objects.isNull(currentParkedVehicle))
            throw new VehicleNotFoundException();
        else
            return currentParkedVehicle;
    }


    @PostMapping("/checkin")
    public ResponseEntity<Parking> checkInToParkingLot(@RequestBody @Valid Vehicle vehicle)
    {
        return new ResponseEntity<>(parkingService.createParkingRecord(vehicle), HttpStatus.CREATED);
    }

    @PutMapping("/checkout/{id}")
    public ResponseEntity<Parking> checkOutFromParkingLot(@PathVariable int id)
    {
        return new ResponseEntity<>(parkingService.updateParkingRecord(id), HttpStatus.OK);
    }
}

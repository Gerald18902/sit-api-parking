package com.parking.apiparking.controller;

import com.parking.apiparking.entities.Car;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.parking.apiparking.services.ParkingService;

import java.util.List;
import java.util.Optional;

@RestController //Notación que indica el comportamiento al acceso a los verbos hhtp a los status de respuesta y cada metodo tendra una URL que identificará al recurso que quiere acceder
@RequestMapping("/parking") //parking -- URL

public class ParkingController {

    private final ParkingService parkingService;

    public ParkingController(ParkingService parkingService){
        this.parkingService = parkingService;
    }

    //GET: http://dominio/parkings/cars
    @GetMapping("/cars")
    public ResponseEntity<List<Car>> getAllCars(){
        return ResponseEntity.ok(this.parkingService.getAllCars());
    }
    @GetMapping("/cars/{licensePlate}")
    public ResponseEntity<Car> getCarByLicensePlate(@PathVariable String licensePlate){
        Optional<Car> optionalCar = this.parkingService.getCarByLicensePlate(licensePlate);
        if(optionalCar.isPresent()){
            return ResponseEntity.ok(optionalCar.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/carsColor")
    public ResponseEntity<List<Car>> getCarsByColor(@RequestParam(required = false) String color){
        if(color !=null){
            List<Car> carsByColor = this.parkingService.gerCarsByColor(color);
            return ResponseEntity.ok(carsByColor);
        }
        return ResponseEntity.ok(this.parkingService.getAllCars());
    }

    //POST: http://dominio/parkings/cars
    @PostMapping("/cars")
    public ResponseEntity<Car> addCars(@RequestBody Car car){
        this.parkingService.addCar(car);
        return new ResponseEntity<>(car, HttpStatus.CREATED);
    }

    @DeleteMapping("/cars/{licensePlate}")
    public ResponseEntity<Void> removeCarByLicensePlate(@PathVariable String licensePlate){
        boolean removed = this.parkingService.removeCarByLicensePlate(licensePlate);
        if(removed){
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/cars/{licensePlate}/entry")
    public ResponseEntity<Void> registerEntry(@PathVariable String licensePlate){
        Car car = new Car(licensePlate,null);
        this.parkingService.parkCar(car);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @PostMapping("/cars/{licensePlate}/exit")
    public ResponseEntity<Double> registerExit(@PathVariable String licensePlate){
        double fee = this.parkingService.calculateParkingFee(licensePlate);
        this.parkingService.unparkCar(licensePlate);
        return ResponseEntity.ok(fee);
    }

}
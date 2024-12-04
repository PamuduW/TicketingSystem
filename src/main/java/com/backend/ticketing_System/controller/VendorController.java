package com.backend.ticketing_System.controller;

import com.backend.ticketing_System.model.Vendor;
import com.backend.ticketing_System.service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class VendorController {
    private final VendorService vendorService;

    @Autowired
    public VendorController(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    @GetMapping("/vendors")
    public ResponseEntity<List<Vendor>> getAllVendors() {
        return ResponseEntity.ok(vendorService.getAllVendors());
    }

    @GetMapping("/vendor")
    public ResponseEntity<Vendor> getVendorByUsername(@RequestParam String username, @RequestParam String pass) {
        Optional<Vendor> optionalVendor = vendorService.getVendorByUsername(username);
        if (optionalVendor.isPresent()){
            if (optionalVendor.get().getPass().equals(pass))
                return ResponseEntity.ok(optionalVendor.get());
            else return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/vendor")
    public ResponseEntity<?> createVendor(@RequestBody Vendor vendor) {
        try {
            return new ResponseEntity<>(vendorService.createVendor(vendor), HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/vendor/{id}")
    public ResponseEntity<?> getVendorById(@PathVariable String id) {
        Optional<Vendor> optionalVendor = vendorService.getVendorById(id);
        if (optionalVendor.isPresent())
            return ResponseEntity.ok(optionalVendor.get());
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/vendor/{id}")
    public ResponseEntity<?> updateVendor(@PathVariable String id, @RequestBody Vendor vendorDetails) {
        try {
            Vendor updatedVendor = vendorService.updateVendor(id, vendorDetails);
            return ResponseEntity.ok(updatedVendor);
        } catch (IOException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @DeleteMapping("/vendor/{id}")
    public ResponseEntity<?> deleteVendor(@PathVariable String id) {
        try {
            vendorService.deleteVendor(id);
            return ResponseEntity.noContent().build();
        } catch (IOException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }
}
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

/**
 * REST controller for managing vendors.
 */
@RestController
@RequestMapping("/api")
public class VendorController {
    private final VendorService vendorService;

    /**
     * Constructor for VendorController.
     *
     * @param vendorService the service to manage vendors.
     */
    @Autowired
    public VendorController(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    /**
     * Get all vendors.
     *
     * @return a ResponseEntity containing a list of all vendors.
     */
    @GetMapping("/vendors")
    public ResponseEntity<List<Vendor>> getAllVendors() {
        return ResponseEntity.ok(vendorService.getAllVendors());
    }

    /**
     * Get a vendor by username and password.
     *
     * @param username the username of the vendor.
     * @param pass the password of the vendor.
     * @return the vendor if found and password matches, otherwise appropriate HTTP status.
     */
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

    /**
     * Create a new vendor.
     *
     * @param vendor the vendor to create.
     * @return the created vendor or an error message if creation fails.
     */
    @PostMapping("/vendor")
    public ResponseEntity<?> createVendor(@RequestBody Vendor vendor) {
        try {
            return new ResponseEntity<>(vendorService.createVendor(vendor), HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    /**
     * Get a vendor by ID.
     *
     * @param id the ID of the vendor to retrieve.
     * @return the vendor if found, otherwise appropriate HTTP status.
     */
    @GetMapping("/vendor/{id}")
    public ResponseEntity<?> getVendorById(@PathVariable String id) {
        Optional<Vendor> optionalVendor = vendorService.getVendorById(id);
        if (optionalVendor.isPresent())
            return ResponseEntity.ok(optionalVendor.get());
        return ResponseEntity.notFound().build();
    }

    /**
     * Update an existing vendor.
     *
     * @param id the ID of the vendor to update.
     * @param vendorDetails the new details of the vendor.
     * @return the updated vendor or an error message if update fails.
     */
    @PutMapping("/vendor/{id}")
    public ResponseEntity<?> updateVendor(@PathVariable String id, @RequestBody Vendor vendorDetails) {
        try {
            Vendor updatedVendor = vendorService.updateVendor(id, vendorDetails);
            return ResponseEntity.ok(updatedVendor);
        } catch (IOException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    /**
     * Delete a vendor.
     *
     * @param id the ID of the vendor to delete.
     * @return no content if deletion is successful, otherwise an error message.
     */
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
package com.backend.ticketing_System.service;

import com.backend.ticketing_System.model.Vendor;
import com.backend.ticketing_System.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Service class for managing vendors.
 */
@Service
public class VendorService {
    private final VendorRepository vendorRepo;
    private final ReentrantLock lock = new ReentrantLock(true);

    /**
     * Constructs a new VendorService with the given VendorRepository.
     *
     * @param vendorRepo the repository for vendor data.
     */
    @Autowired
    public VendorService(VendorRepository vendorRepo){
        this.vendorRepo = vendorRepo;
    }

    /**
     * Retrieves all vendors.
     *
     * @return a list of all vendors.
     */
    public List<Vendor> getAllVendors() {
        return vendorRepo.findAll();
    }

    /**
     * Retrieves a vendor by their username.
     *
     * @param vendorUsername the username of the vendor.
     * @return an Optional containing the found vendor, or empty if no vendor was found.
     */
    public Optional<Vendor> getVendorByUsername(String vendorUsername) {
        return vendorRepo.findByUsername(vendorUsername);
    }

    /**
     * Retrieves a vendor by their ID.
     *
     * @param vendorId the ID of the vendor.
     * @return an Optional containing the found vendor, or empty if no vendor was found.
     */
    public Optional<Vendor> getVendorById(String vendorId) {
        return vendorRepo.findById(vendorId);
    }

    /**
     * Creates a new vendor.
     *
     * @param vendor the vendor to create.
     * @return the created vendor.
     * @throws IOException if a vendor with the same username already exists.
     */
    public Vendor createVendor(Vendor vendor) throws IOException {
        lock.lock();
        try {
            if (vendorRepo.findByUsername(vendor.getUsername()).isPresent())
                throw new IOException("Vendor with the same username already exists");
            return vendorRepo.save(vendor);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Updates an existing vendor.
     *
     * @param vendorId the ID of the vendor to update.
     * @param vendorDetails the new details for the vendor.
     * @return the updated vendor.
     * @throws IOException if the vendor is not found.
     */
    public Vendor updateVendor(String vendorId, Vendor vendorDetails) throws IOException {
        lock.lock();
        try {
            return vendorRepo.findById(vendorId)
                    .map(existingVendor -> {
                        existingVendor.setUsername(vendorDetails.getUsername());
                        existingVendor.setPass(vendorDetails.getPass());
                        return vendorRepo.save(existingVendor);
                    })
                    .orElseThrow(() -> new IOException("Vendor not found with id " + vendorId));
        } finally {
            lock.unlock();
        }
    }

    /**
     * Deletes a vendor.
     *
     * @param vendorId the ID of the vendor to delete.
     * @throws IOException if the vendor is not found.
     */
    public void deleteVendor(String vendorId) throws IOException {
        lock.lock();
        try {
            if (vendorRepo.findById(vendorId).isEmpty())
                throw new IOException("This vendor does not exist");
            vendorRepo.deleteById(vendorId);
        } finally {
            lock.unlock();
        }
    }
}
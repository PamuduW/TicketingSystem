package com.backend.ticketing_System.service;

import com.backend.ticketing_System.model.Vendor;
import com.backend.ticketing_System.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class VendorService {
    private final VendorRepository vendorRepo;
    private final ReentrantLock lock = new ReentrantLock(true);

    @Autowired
    public VendorService(VendorRepository vendorRepo){
        this.vendorRepo = vendorRepo;
    }

    public List<Vendor> getAllVendors() {
        return vendorRepo.findAll();
    }

    public Optional<Vendor> getVendorByUsername(String vendorUsername) {
        return vendorRepo.findByUsername(vendorUsername);
    }

    public Optional<Vendor> getVendorById(String vendorId) {
        return vendorRepo.findById(vendorId);
    }

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
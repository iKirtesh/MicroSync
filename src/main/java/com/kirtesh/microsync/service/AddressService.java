package com.kirtesh.microsync.service;

import com.kirtesh.microsync.exception.AddressNotFoundException;
import com.kirtesh.microsync.exception.UserNotFoundException;
import com.kirtesh.microsync.model.Address;
import com.kirtesh.microsync.model.Users;
import com.kirtesh.microsync.repository.AddressRepository;
import com.kirtesh.microsync.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    public AddressService(UserRepository userRepository, AddressRepository addressRepository) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
    }


    public List<Address> getAddressesByUsername(String username) {
        return addressRepository.findByUsername(username);
    }

    @Transactional
    public Address addAddress(String username, Address address) {
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        address.setUser(user);
        return addressRepository.save(address);
    }

    @Transactional
    public Address updateAddress(String username, Long addressId, Address updatedAddress) {
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        Address existingAddress = addressRepository.findByIdAndUserId(addressId, user.getId())
                .orElseThrow(() -> new AddressNotFoundException("Address not found"));

        // Update fields
        existingAddress.updateFrom(updatedAddress);

        if (updatedAddress.isDefault()) {
            unsetDefaultAddress(user.getId());
            existingAddress.setDefault(true);
        }

        return addressRepository.save(existingAddress);
    }

    @Transactional
    public Address setDefaultAddress(String username, Long addressId) {
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        unsetDefaultAddress(user.getId());

        Address address = addressRepository.findByIdAndUserId(addressId, user.getId())
                .orElseThrow(() -> new AddressNotFoundException("Address not found"));
        address.setDefault(true);

        return addressRepository.save(address);
    }

    private void unsetDefaultAddress(Long userId) {
        addressRepository.unsetDefaultForUser(userId);
    }

    @Transactional
    public void deleteAddress(String username, Long addressId) {
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Address address = addressRepository.findByIdAndUserId(addressId, user.getId())
                .orElseThrow(() -> new AddressNotFoundException("Address not found"));

        addressRepository.delete(address);
    }
}

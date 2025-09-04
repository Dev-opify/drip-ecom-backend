package com.aditi.dripyard.repository;

import com.aditi.dripyard.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}

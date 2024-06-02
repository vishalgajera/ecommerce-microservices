package com.user.repository;

import com.user.entities.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AddressRepository  extends JpaRepository<Address, Long> {
    List<Address> findByUserId(Long userId);

}
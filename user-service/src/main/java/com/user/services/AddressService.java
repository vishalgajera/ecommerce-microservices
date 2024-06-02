package com.user.services;

import com.user.dto.AddressDTO;
import com.user.dto.RestResponse;
import org.springframework.http.ResponseEntity;

public interface AddressService {
    ResponseEntity<RestResponse> create(AddressDTO addressDTO);
    ResponseEntity<RestResponse> update(AddressDTO addressDTO);
    ResponseEntity<RestResponse> delete(Long addressId);
    ResponseEntity<RestResponse> getAll();
    ResponseEntity<RestResponse> getOne(Long addressId);
}

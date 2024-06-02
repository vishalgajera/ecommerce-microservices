package com.user.controller;

import com.user.constant.EndPointUriConstant;
import com.user.dto.AddressDTO;
import com.user.dto.RestResponse;
import com.user.services.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(EndPointUriConstant.USER_ADDRESS_COMMON_MAPPING)
public class AddressController {

    @Autowired
    private AddressService addressService;

    @GetMapping
    public ResponseEntity<RestResponse> getAll() {
        return addressService.getAll();
    }

    @GetMapping("/{addressId}")
    public ResponseEntity<RestResponse> getOne(@PathVariable Long addressId) {
        return addressService.getOne(addressId);
    }

    @PostMapping
    public ResponseEntity<RestResponse> create(@RequestBody AddressDTO addressDTO) {
        return addressService.create(addressDTO);
    }

    @PutMapping
    public ResponseEntity<RestResponse> update(@RequestBody AddressDTO addressDTO) {
        return addressService.update(addressDTO);
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<RestResponse> delete(@PathVariable Long addressId) {
        return addressService.delete(addressId);
    }
}

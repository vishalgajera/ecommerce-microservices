package com.user.services.impl;

import com.user.dto.AddressDTO;
import com.user.dto.RestResponse;
import com.user.entities.Address;
import com.user.repository.AddressRepository;
import com.user.services.AddressService;
import com.user.utility.JwtTokenHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private JwtTokenHandler jwtTokenHandler;

    @Override
    public ResponseEntity<RestResponse> create(AddressDTO addressDTO) {
        Address address = new Address();
        address.setUserId(jwtTokenHandler.fetchCurrentUserId());
        address.setFullName(addressDTO.getFullName());
        address.setStreet_line_1(addressDTO.getStreet_line_1());
        address.setStreet_line_2(addressDTO.getStreet_line_2());
        address.setArea(addressDTO.getArea());
        address.setCity(addressDTO.getCity());
        address.setState(addressDTO.getState());
        address.setCountry(addressDTO.getCountry());
        address.setPinCode(addressDTO.getPinCode());
        address.setContactNo(addressDTO.getContactNo());
        address = addressRepository.save(address);
        return prepareResponseEntity(address);
    }

    @Override
    public ResponseEntity<RestResponse> update(AddressDTO addressDTO) {
        Address address = addressRepository.findById(addressDTO.getId()).orElseThrow(() -> new RuntimeException("Address not found with id= "+addressDTO.getId()+"!!!"));
        address.setFullName(addressDTO.getFullName());
        address.setStreet_line_1(addressDTO.getStreet_line_1());
        address.setStreet_line_2(addressDTO.getStreet_line_2());
        address.setArea(addressDTO.getArea());
        address.setCity(addressDTO.getCity());
        address.setState(addressDTO.getState());
        address.setCountry(addressDTO.getCountry());
        address.setPinCode(addressDTO.getPinCode());
        address.setContactNo(addressDTO.getContactNo());
        Address updatedAddress = addressRepository.save(address);
        return prepareResponseEntity(updatedAddress);
    }

    @Override
    public ResponseEntity<RestResponse> delete(Long addressId) {
        addressRepository.deleteById(addressId);
        return prepareResponseEntity("Address Successfully Deleted");
    }

    @Override
    public ResponseEntity<RestResponse> getAll() {
        List<Address> allAddress = addressRepository.findByUserId(jwtTokenHandler.fetchCurrentUserId());
        return prepareResponseEntity(allAddress);
    }

    @Override
    public ResponseEntity<RestResponse> getOne(Long addressId) {
        Address address = addressRepository.findById(addressId).orElseThrow(() -> new RuntimeException("Address not found with id= "+addressId+"!!!"));
        return prepareResponseEntity(address);
    }

    ResponseEntity<RestResponse> prepareResponseEntity(Object response) {
        RestResponse restResponse = new RestResponse("success", response);
        return new ResponseEntity<>(restResponse, HttpStatus.OK);
    }
}

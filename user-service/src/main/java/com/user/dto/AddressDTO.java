package com.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressDTO {
    private long id;

    private String fullName;

    private String street_line_1;

    private String street_line_2;

    private String area;

    private String city;

    private String state;

    private String country;

    private String pinCode;

    private String contactNo;
}

package com.user.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "address")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private Long userId;

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

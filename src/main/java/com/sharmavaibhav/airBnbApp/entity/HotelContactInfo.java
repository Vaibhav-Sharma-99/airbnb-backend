package com.sharmavaibhav.airBnbApp.entity;


import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable //means- the columns here will be put(embedded) into Hotel table only
public class HotelContactInfo {
    private String address;
    private String phoneNumber;
    private String email;
    private String Location;
}

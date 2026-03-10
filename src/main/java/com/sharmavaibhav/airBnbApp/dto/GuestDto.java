package com.sharmavaibhav.airBnbApp.dto;

import com.sharmavaibhav.airBnbApp.entity.User;
import com.sharmavaibhav.airBnbApp.entity.enums.Gender;
import jakarta.persistence.*;
import lombok.Data;

@Data
public class GuestDto {

    private long id;
    private User user;
    private String name;
    private Gender gender;
    private Integer age;
}

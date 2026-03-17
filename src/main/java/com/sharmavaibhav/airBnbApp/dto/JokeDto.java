package com.sharmavaibhav.airBnbApp.dto;

import lombok.Data;

@Data
public class JokeDto {

    String text;
    String category;
    String laughScore;
    Boolean isNSFW;
}

package com.sharmavaibhav.airBnbApp.service;


import com.sharmavaibhav.airBnbApp.dto.HotelDto;
import com.sharmavaibhav.airBnbApp.dto.HotelInfoDto;
import com.sharmavaibhav.airBnbApp.entity.Hotel;
import org.jspecify.annotations.Nullable;

public interface HotelService {

    HotelDto createNewHotel(HotelDto hotelDto);

    HotelDto getHotelById(Long id);

    HotelDto updateHotelById(Long id, HotelDto hotelDto);

    void deleteHotelById(Long id);

    String activateHotel(Long id);

    HotelInfoDto getHotelInfoById(Long hotelId);
}

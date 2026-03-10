package com.sharmavaibhav.airBnbApp.controller;


import com.sharmavaibhav.airBnbApp.dto.HotelDto;
import com.sharmavaibhav.airBnbApp.dto.HotelInfoDto;
import com.sharmavaibhav.airBnbApp.dto.HotelPriceDto;
import com.sharmavaibhav.airBnbApp.dto.HotelSearchRequest;
import com.sharmavaibhav.airBnbApp.service.HotelService;
import com.sharmavaibhav.airBnbApp.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hotels")
@RequiredArgsConstructor
@Slf4j
public class HotelBrowseController {

    private final InventoryService inventoryService;
    private final HotelService hotelService;

    @GetMapping("/search")
    public ResponseEntity<Page<HotelPriceDto>> searchHotel(@RequestBody HotelSearchRequest hotelSearchRequest){

        System.out.println(hotelSearchRequest);
        Page<HotelPriceDto> page = inventoryService.searchHotels(hotelSearchRequest);

        return ResponseEntity.ok(page);
    }

    @GetMapping("/{hotelId}/info")
    public ResponseEntity<HotelInfoDto> getHotelInfo(@PathVariable Long hotelId){
        log.info("Fetching Hotel Info for id: "+ hotelId);
        HotelInfoDto hotelInfoDto = hotelService.getHotelInfoById(hotelId);

        return ResponseEntity.ok(hotelInfoDto);
    }

}



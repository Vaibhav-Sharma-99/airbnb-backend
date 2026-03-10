package com.sharmavaibhav.airBnbApp.service;


import com.sharmavaibhav.airBnbApp.dto.HotelDto;
import com.sharmavaibhav.airBnbApp.dto.HotelPriceDto;
import com.sharmavaibhav.airBnbApp.dto.HotelSearchRequest;
import com.sharmavaibhav.airBnbApp.entity.Hotel;
import com.sharmavaibhav.airBnbApp.entity.Inventory;
import com.sharmavaibhav.airBnbApp.entity.Room;
import com.sharmavaibhav.airBnbApp.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ModelMapper modelMapper;

    @Override
    public void initializeRoomForAYear(Room room){
        log.info("Starting Initialzation Inventory for room ID:" + room.getId());
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusYears(1);

        for(; !today.isAfter(endDate); today=today.plusDays(1)){
            Inventory inventory = Inventory.builder()
                    .hotel(room.getHotel())
                    .room(room)
                    .bookedCount(0)
                    .city(room.getHotel().getCity())
                    .date(today)
                    .price(room.getBasePrice())
                    .surgeFactor(BigDecimal.ONE)
                    .totalCount(room.getTotalCount())
                    .isClosed(false)
                    .build();

            inventoryRepository.save(inventory);
            log.info("Ending Initialzation Inventory for room ID:" + room.getId());
        }
    }

    @Override
    public void deleteFutureInventories(Hotel hotel) {

    }

    @Override
    public void deleteFutureInventories(Room room) {
        log.info("Starting inventory deleteion for Room ID " + room.getId());
        LocalDate today = LocalDate.now();
        inventoryRepository.deleteByRoom(room);
    }



    @Override
    public Page<HotelPriceDto> searchHotels(HotelSearchRequest hotelSearchRequest) {
        Pageable pageable = PageRequest.of(hotelSearchRequest.getPage(), hotelSearchRequest.getSize());
// we need to get all the hotels in the city having room Inventory on those days
        Long dateCount = ChronoUnit.DAYS.
                between(hotelSearchRequest.getStartDate(), hotelSearchRequest.getEndDate());

        Page<Hotel> hotelPage = inventoryRepository.findHotelsWithAvailableInventory(
                hotelSearchRequest.getCity(),
                hotelSearchRequest.getStartDate(),
                hotelSearchRequest.getEndDate(),
                hotelSearchRequest.getRoomsCount(),
                dateCount,
                pageable
                );
//        Each element of the hotelPage is a Hotel so below convert each to HotelDto
        return hotelPage.map((element) -> modelMapper.map(element, HotelPriceDto.class));
    }


}

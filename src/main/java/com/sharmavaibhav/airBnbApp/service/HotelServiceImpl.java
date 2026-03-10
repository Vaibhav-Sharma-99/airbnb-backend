package com.sharmavaibhav.airBnbApp.service;


import com.sharmavaibhav.airBnbApp.dto.HotelDto;
import com.sharmavaibhav.airBnbApp.dto.HotelInfoDto;
import com.sharmavaibhav.airBnbApp.dto.RoomDto;
import com.sharmavaibhav.airBnbApp.entity.Hotel;
import com.sharmavaibhav.airBnbApp.entity.Room;
import com.sharmavaibhav.airBnbApp.entity.User;
import com.sharmavaibhav.airBnbApp.exceptions.ResourceNotFoundException;
import com.sharmavaibhav.airBnbApp.exceptions.UnAuthorisedException;
import com.sharmavaibhav.airBnbApp.repository.HotelRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class HotelServiceImpl implements HotelService{

    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;
    private final InventoryService inventoryService;

    @Autowired
    public HotelServiceImpl(HotelRepository hotelRepository, ModelMapper modelMapper, InventoryService inventoryService) {
        this.hotelRepository = hotelRepository;
        this.modelMapper = modelMapper;
        this.inventoryService = inventoryService;
    }

    @Override
    public HotelDto createNewHotel(HotelDto hotelDto){
        log.info("Creating a new hotel with name: {}", hotelDto.getName());

        Hotel hotel = modelMapper.map(hotelDto, Hotel.class);
        hotel.setActive(false);

        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        hotel.setOwner(user);

        hotel = hotelRepository.save(hotel);
        log.info("Created a new hotel with name: {}", hotelDto.getName());

        return modelMapper.map(hotel, HotelDto.class);
    }

    @Override
    public HotelDto getHotelById(Long id){
        log.info("Getting Hotel by id {} ", id);

        Hotel hotel = hotelRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel Not Found with id: "+ id));
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(!user.equals(hotel.getOwner())){
            throw new UnAuthorisedException("This user does not own this hotel- tho i dont know why master ji cares anyone should be able to get hotl");
        }

        return modelMapper.map(hotel, HotelDto.class);
    }

    @Override
    public HotelDto updateHotelById(Long id, HotelDto hotelDto) {
        Hotel hotel = hotelRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel Not Found with ID: " + id));

        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(!user.equals(hotel.getOwner())){
            throw new UnAuthorisedException("This user does not own this hotel- tho i dont know why master ji cares anyone should be able to get hotl");
        }

        modelMapper.map(hotelDto, hotel);
        hotel.setId(id);
        hotel = hotelRepository.save(hotel);
        return modelMapper.map(hotel, HotelDto.class);
    }

    @Override
    @Transactional
    public void deleteHotelById(Long id) {

        Hotel hotel = hotelRepository
                .findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("No Hotel Found"));

        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(hotel.getOwner())){
            throw new UnAuthorisedException("This user does not own this hotel- tho i dont know why master ji cares anyone should be able to get hotl");
        }

        for(Room room: hotel.getRooms()){
            inventoryService.deleteFutureInventories(room);
//            TODO: master has inventoryService.deleteAllInventories(room); roomRepository.deleteById(room.getId())
        }
        hotelRepository.deleteById(id);

    }

    @Override
    @Transactional
    public String activateHotel(Long id){
        Hotel hotel = hotelRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel Not Found with ID: " + id));

        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(!user.equals(hotel.getOwner())){
            throw new UnAuthorisedException("This user does not own this hotel- tho i dont know why master ji cares anyone should be able to get hotl");
        }

        hotel.setActive(true);
//        Assuming this is the first Time hotel activation after creation

        for(Room room: hotel.getRooms()){
            inventoryService.initializeRoomForAYear(room);
        }


        return "Hotel Activated i think";
    }

    @Override
    public HotelInfoDto getHotelInfoById(Long hotelId) {
        log.info("Starting getHotelInfoById for Id " + hotelId);
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel Not Found with ID: "+hotelId));


        List<RoomDto> rooms = hotel.getRooms()
                .stream()
                .map((element) -> modelMapper.map(element, RoomDto.class))
                .toList();

        return new HotelInfoDto(modelMapper.map(hotel, HotelDto.class), rooms);
    }


}

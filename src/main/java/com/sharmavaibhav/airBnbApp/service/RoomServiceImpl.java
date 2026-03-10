package com.sharmavaibhav.airBnbApp.service;

import com.sharmavaibhav.airBnbApp.dto.RoomDto;
import com.sharmavaibhav.airBnbApp.entity.Hotel;
import com.sharmavaibhav.airBnbApp.entity.Room;
import com.sharmavaibhav.airBnbApp.entity.User;
import com.sharmavaibhav.airBnbApp.exceptions.ResourceNotFoundException;
import com.sharmavaibhav.airBnbApp.exceptions.UnAuthorisedException;
import com.sharmavaibhav.airBnbApp.repository.HotelRepository;
import com.sharmavaibhav.airBnbApp.repository.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomServiceImpl implements RoomService{
    private final ModelMapper modelMapper;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final InventoryService inventoryService;

    @Override
    @Transactional
    public RoomDto createNewRoom(Long hotelId, RoomDto roomDto) {
        log.info("Creating a new Room in Hotel with ID: " + hotelId);
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with Id: "+hotelId));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(hotel.getOwner())){
            throw new UnAuthorisedException("This user does not own this hotel-so no create Room");
        }

        Room room = modelMapper.map(roomDto, Room.class);
        room.setHotel(hotel);
        room = roomRepository.save(room);

        if(hotel.isActive()){
            log.info("Calling initializeRoomForAYear");
            inventoryService.initializeRoomForAYear(room);
        }

        return modelMapper.map(room, RoomDto.class);
    }

    @Override
    public List<RoomDto> getAllRoomsInHotel(Long hotelId) {
        log.info("Getting all Room for Hotel id: " + hotelId);

        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("No Hotel Found for ID: "+hotelId));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(hotel.getOwner())){
            throw new UnAuthorisedException("This user does not own this hotel-so no create Room");
        }

        return hotel.getRooms()
                .stream()
                .map((element) -> modelMapper.map(element, RoomDto.class))
                .collect(Collectors.toList());

    }

    @Override
    public RoomDto getRoomById(Long roomId) {
        log.info("Getting Room by Id: "+ roomId);

        Room room = roomRepository
                .findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("No Room found with Id "+roomId));
        return modelMapper.map(room, RoomDto.class);
    }

    @Override
    @Transactional
    public void deleteRoomById(Long roomId) {
        log.info("Deleting the Room with ID "+roomId);

        boolean exists = roomRepository.existsById(roomId);
        if(!exists){
            throw new ResourceNotFoundException("Room not Found with ID "+roomId);
        }
        Room room = roomRepository.findById(roomId)
                        .orElseThrow(() -> new ResourceNotFoundException("No Room Found"));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(room.getHotel().getOwner())){
            throw new UnAuthorisedException("This user does not own this -so no Delete Room");
        }

        log.info("Stating Inventory Deletion - calling deleteFutureInventories");
        inventoryService.deleteFutureInventories(room);
        log.info("Ended Inventory Deletion - deleting room now");
        roomRepository.deleteById(roomId);

    }
}

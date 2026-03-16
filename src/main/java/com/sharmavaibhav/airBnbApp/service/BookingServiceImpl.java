package com.sharmavaibhav.airBnbApp.service;


import com.sharmavaibhav.airBnbApp.dto.BookingDto;
import com.sharmavaibhav.airBnbApp.dto.BookingRequest;
import com.sharmavaibhav.airBnbApp.dto.GuestDto;
import com.sharmavaibhav.airBnbApp.dto.HotelDto;
import com.sharmavaibhav.airBnbApp.entity.*;
import com.sharmavaibhav.airBnbApp.entity.enums.BookingStatus;
import com.sharmavaibhav.airBnbApp.exceptions.ResourceNotFoundException;
import com.sharmavaibhav.airBnbApp.exceptions.UnAuthorisedException;
import com.sharmavaibhav.airBnbApp.repository.*;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService{

    private final HotelRepository hotelRepository;
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final InventoryRepository inventoryRepository;
    private final ModelMapper modelMapper;
    private final GuestRepository guestRepository;
    private final CheckoutService checkoutService;

    @Value("${frontend.url}")
    private String frontEndUrl;

    @Override
    @Transactional
    public BookingDto initializeBooking(BookingRequest bookingRequest) {
        log.info("Initializing Booking-starting initializeBooking");

        Hotel hotel = hotelRepository.findById(bookingRequest.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("Hotel Not Found with ID: " + bookingRequest.getHotelId()));

        Room room  = roomRepository.findById(bookingRequest.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room Not Found with ID: " + bookingRequest.getRoomId()));

        //Should LOCK the inventory so no other user can book it for the same date for now

        List<Inventory> inventoryList = inventoryRepository.findAndLockAvailableInventory(
                bookingRequest.getRoomId(), bookingRequest.getCheckInDate(),
                bookingRequest.getCheckOutDate(), bookingRequest.getRoomsCount());

        Long daysCount = ChronoUnit.DAYS.between(bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate()) + 1;

        log.info("inventoryList size " + inventoryList.size() + " " + daysCount);

        inventoryList.stream()
                .forEach((element) -> System.out.println(element));

        if(inventoryList.size() <= daysCount) {
            throw new IllegalStateException("Room is not available anymore");
        }

        for(Inventory inventory: inventoryList){
            inventory.setBookedCount(inventory.getBookedCount() + bookingRequest.getRoomsCount());
            inventory.setIsClosed(true);
            inventoryRepository.saveAll(inventoryList);
        }

        //temp --getting dummy user
        //TODO remove dummy user
        User user = new User();
        user.setId(1L);

        //TODO calculate dynamic price

        //Create Booking Object
        Booking booking = Booking.builder()
                .bookingStatus(BookingStatus.RESERVED)
                .hotel(hotel)
                .room(room)
                .checkInDate(bookingRequest.getCheckInDate())
                .checkOutDate(bookingRequest.getCheckOutDate())
                .user(user)
                .roomCount(bookingRequest.getRoomsCount())
                .cost(BigDecimal.TEN)
                .build();

        booking = bookingRepository.save(booking);
        return modelMapper.map(booking, BookingDto.class);
    }

    @Override
    public BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList) {
        log.info("Adding Guests for Booking Id: "+ bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel Not Found with ID: " + bookingId));
        User user = getCurrentUser();

        if(!user.equals(booking.getUser())){
            throw new UnAuthorisedException("Booking does not belong to this user id: " + user.getId());
        }

       if (isBookingExpired(booking)){
           throw new IllegalStateException("Booking has already Expired");
       }
       if(booking.getBookingStatus() != BookingStatus.RESERVED){
           throw new IllegalStateException("Booking is not under Reserved State");
       }

       for (GuestDto guestDto: guestDtoList){
           Guest guest = modelMapper.map(guestDto, Guest.class);
           guest.setUser(user);

           guest = guestRepository.save(guest);
           booking.getGuests().add(guest);
       }

       booking.setBookingStatus(BookingStatus.GUEST_ADDED);
       booking = bookingRepository.save(booking);

       return modelMapper.map(booking, BookingDto.class);
    }

    public Boolean isBookingExpired(Booking booking){
        return booking.getCreatedAt().plusMinutes(10).isBefore(LocalDateTime.now());

    }

    public User getCurrentUser(){
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Override
    public String initiatePayment(Long bookingId){
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("No booking Found for Id: "+ bookingId));

        User user = getCurrentUser();

        if(!user.equals(booking.getUser())){
            throw new UnAuthorisedException("Booking does not belong to this user id: " + user.getId());
        }

        if (isBookingExpired(booking)){
            throw new IllegalStateException("Booking has already Expired");
        }

        String sessionUrl = checkoutService.getCheckoutSession(booking, frontEndUrl+"/payments/success",
                frontEndUrl+"/payments/failure");
        booking.setBookingStatus(BookingStatus.PAYMENT_PENDING);
        bookingRepository.save(booking);

        return sessionUrl;
    }

}

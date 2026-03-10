package com.sharmavaibhav.airBnbApp.service;

import com.sharmavaibhav.airBnbApp.dto.BookingDto;
import com.sharmavaibhav.airBnbApp.dto.BookingRequest;
import com.sharmavaibhav.airBnbApp.dto.GuestDto;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface BookingService {

    BookingDto initializeBooking(BookingRequest bookingRequest);

    BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList);

    String initiatePayment(Long bookingId);
}

package com.sharmavaibhav.airBnbApp.repository;

import com.sharmavaibhav.airBnbApp.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {


}

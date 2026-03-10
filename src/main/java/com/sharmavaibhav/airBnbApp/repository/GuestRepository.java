package com.sharmavaibhav.airBnbApp.repository;


import com.sharmavaibhav.airBnbApp.entity.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestRepository extends JpaRepository<Guest, Long> {
}

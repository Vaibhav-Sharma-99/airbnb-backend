package com.sharmavaibhav.airBnbApp.service;

import com.sharmavaibhav.airBnbApp.entity.Booking;

public interface CheckoutService {

    String getCheckoutSession(Booking booking, String successUrl, String failureUrl);


}

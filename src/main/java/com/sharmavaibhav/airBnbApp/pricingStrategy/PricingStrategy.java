package com.sharmavaibhav.airBnbApp.pricingStrategy;

import com.sharmavaibhav.airBnbApp.entity.Inventory;

import java.math.BigDecimal;

public interface PricingStrategy {

    BigDecimal calculatePrice(Inventory inventory);
}

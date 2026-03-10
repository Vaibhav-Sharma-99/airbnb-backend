package com.sharmavaibhav.airBnbApp.pricingStrategy;


import com.sharmavaibhav.airBnbApp.entity.Inventory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PricingService {

    public BigDecimal calculateDynamicPricing(Inventory inventory){
        PricingStrategy pricingStrategy = new BasePricingStrategy();

        //Apply Additional Strategy;
        pricingStrategy = new SurgePricingStrategy(pricingStrategy);
        pricingStrategy = new OccupancyPricingStrategy(pricingStrategy);
        pricingStrategy = new UrgencyPricingStrategy(pricingStrategy);
        pricingStrategy = new HolidayPricingStrategy(pricingStrategy);

//TODO - I dont think this is correct, we are just passing the wrapped object each time, so Holiday has wrapped = Urgency
//        but we are still calculating the price only once at end
        return pricingStrategy.calculatePrice(inventory);

    }
}

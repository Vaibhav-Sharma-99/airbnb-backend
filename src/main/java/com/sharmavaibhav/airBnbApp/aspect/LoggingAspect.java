package com.sharmavaibhav.airBnbApp.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;


@Component
@Slf4j
@Aspect
public class LoggingAspect {

    @Before("execution (* com.sharmavaibhav.airBnbApp.service.BookingServiceImpl.*(..))")
    public void beforeBookingServiceImplMethods(JoinPoint joinPoint,
                                                Object returnedObject){
        System.out.println(returnedObject); // data returned by targeted method
        log.info("Before method call: {}", joinPoint.getSignature());
    }

}

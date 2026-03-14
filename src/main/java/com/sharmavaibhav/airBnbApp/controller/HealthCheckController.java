package com.sharmavaibhav.airBnbApp.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class HealthCheckController {


    @GetMapping("/")
    public ResponseEntity<?> healthCheckController(){
        System.out.println("Just printing some");
        return ResponseEntity.ok("OK");
    }
    public void helperMethod(){
        System.out.println("This is just a helper method--- it does nothinggg");
    }

    public void randomPracticeMethod(){
        System.out.println("This is just a Practice methods");
    }
}

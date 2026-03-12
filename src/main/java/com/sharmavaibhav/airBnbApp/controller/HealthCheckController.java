package com.sharmavaibhav.airBnbApp.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {


    @GetMapping("/")
    public ResponseEntity<String> healthCheckController(){
        return ResponseEntity.ok("OK");
    }


    public void helperMethod(){
        System.out.println("This is just a helper method--- it does nothinggg");
    }
}

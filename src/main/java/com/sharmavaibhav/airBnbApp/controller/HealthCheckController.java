package com.sharmavaibhav.airBnbApp.controller;


import com.sharmavaibhav.airBnbApp.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequiredArgsConstructor
public class HealthCheckController {

    private final AiService aiService;

    @GetMapping("/")
    public ResponseEntity<?> healthCheckController(){
        System.out.println("Just printing some");
        String s = aiService.getJoke("fat people");
        return ResponseEntity.ok("This is the default landing Page\n<br>" + s);
    }
    public void helperMethod(){
        System.out.println("This is just a helper method--- it does nothinggg");
    }

    public void randomPracticeMethod(){
        System.out.println("This is just a Practice methods");
    }
}

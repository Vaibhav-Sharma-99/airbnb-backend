package com.sharmavaibhav.airBnbApp.controller;


import com.sharmavaibhav.airBnbApp.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Array;
import java.util.Arrays;
import java.util.stream.Collectors;

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

    @GetMapping("/ai/embed/{text}")
    public ResponseEntity<?> embeddingTester(@PathVariable String text){

        double []theVector = aiService.getEmbedding(text);
        String result = Arrays.stream(theVector)
                        .mapToObj(String::valueOf)
                        .collect(Collectors.joining(", "));

        System.out.println("This is just a Ai Embedding method");
        return ResponseEntity.ok("This is just a Ai Embedding method<br> Your vector is for text-"
                + text + "\n<br>" + result);
    }

    public void randomPracticeMethod(){
        System.out.println("This is just a Practice methods");
    }
}

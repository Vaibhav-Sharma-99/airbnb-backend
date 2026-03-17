package com.sharmavaibhav.airBnbApp.config;

import com.sharmavaibhav.airBnbApp.service.AiService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AiServiceTests {

    @Autowired
    private AiService aiService;

    @Test
    public void getJoke(){
        aiService.getJoke("Fat people");
    }
}

package com.sharmavaibhav.airBnbApp.controller;


import com.sharmavaibhav.airBnbApp.service.AiService;
import com.sharmavaibhav.airBnbApp.service.RAGService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.ai.document.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Array;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class HealthCheckController {

    private final AiService aiService;
    private final RAGService RAGService;

    @GetMapping("/")
    public ResponseEntity<?> healthCheckController(){
        System.out.println("Just printing some");
        String s = aiService.getJoke("fat people");
        return ResponseEntity.ok("This is the default landing Page\n<br>" + s);
    }

    @GetMapping("/ai/search/{text}")
    public ResponseEntity<?> similaritySearch(@PathVariable String text){
        List<Document> matchedMovies = aiService.similaritySearch(text);
        System.out.println(matchedMovies);

        return ResponseEntity.ok(matchedMovies);
    }

    @GetMapping("ai/ask/{text}")
    public ResponseEntity<?> askAi(@PathVariable String text){
        return ResponseEntity.ok(aiService.askAi(text));
    }

    @GetMapping("ai/askRAG/{text}")
    public ResponseEntity<?> askRAGAi(@PathVariable String text){
        return ResponseEntity.ok(RAGService.askRAGAi(text));
    }

    @GetMapping("ai/askRAG/initializeDB")
    public ResponseEntity<?> ingestPdfData(){
        RAGService.ingestPdfToVectorStore();
        return ResponseEntity.ok("Your pdf data has been inserted");
    }

    @GetMapping("/ai/embed/{text}")
    public ResponseEntity<?> embeddingTester(@PathVariable String text){

        double []theVector = aiService.getEmbedding(text);
        String result = Arrays.stream(theVector)
                        .mapToObj(String::valueOf)
                        .collect(Collectors.joining(", "));

//        aiService.ingestDataToVectorStore(text);
        aiService.ingestDataToVectorStore(text);  // saves the data to vector Store db, uses embedding model internally
        System.out.println("This is just a Ai Embedding method");

        return ResponseEntity.ok("This is just a Ai Embedding method<br> Your vector is for text-"
                + text + "\n<br>" + result);
    }

    public void randomPracticeMethod(){
        System.out.println("This is just a Practice methods");
    }
}

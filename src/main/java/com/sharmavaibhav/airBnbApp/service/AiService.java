package com.sharmavaibhav.airBnbApp.service;

import com.sharmavaibhav.airBnbApp.dto.JokeDto;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.LifecycleState;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.DocumentEmbeddingModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class AiService {

    private final ChatClient chatClient;
    private final EmbeddingModel embeddingModel; // implemented by openAi and ollama,,...
    private final VectorStore vectorStore;

    public void ingestDataToVectorStore(String text){
        Document document = new Document(text);
        vectorStore.add(List.of(document)); // Converts to float[] internally
//        List<Document> movies = getMovies();
//        vectorStore.add(movies);
    }

    public String askAi(String prompt){
        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }

    public List<Document> similaritySearch(String text){
//        return vectorStore.similaritySearch(SearchRequest.builder()
//                        .query(text)
//                        .topK(3)    // show the top 3 elements
//                        .similarityThreshold(0.3)
//                .build());

        return vectorStore.similaritySearch(text);
    }

    public double[] getEmbedding(String text){

        float []theVector = embeddingModel.embed(text);
        return IntStream.range(0, theVector.length)
                .mapToDouble(i -> theVector[i]).toArray();

    }

    public String getJoke(String topic){

        String topic2 = "downtrodden";
        String systemPrompt = """
                You are a guy who is funny
                But dont make jokes on {topic}
                """;

        PromptTemplate promptTemplate = new PromptTemplate(systemPrompt);
        String renderedText = promptTemplate.render(Map.of("topic", topic2));

        return chatClient.prompt()
//                .system("You are a funny guy, give a funny joke")
                .system(renderedText)
                .user("Give me a Joke on the topic" + topic)
                .call()
                .content(); //after this api Call is made

//        var aiResponse =  chatClient.prompt()
//                .system(renderedText)
//                .user("Give me a Joke on the topic" + topic)
//                .advisors(new SimpleLoggerAdvisor())  -- for logs
//                .call()
//                .chatClientResponse();
//        return aiResponse.chatResponse().getResult().getOutput().getText();

//        var response = chatClient.prompt()
//                .system(renderedText)
//                .user("Give me a Joke on the topic" + topic)
//                .call()
//                .entity(JokeDto.class);       --springAi checks this and adds text in the prompt to get a JSON response in this format
//        return response.getText();
    }

    public List<Document> getMovies(){
        List<Document> movies = List.of(
                new Document("A thief who steals corporate secrets through dream-sharing technology",
                        Map.of("title", "Inception", "genre", "Sci-Fi", "year", 2010, "director", "Christopher Nolan", "rating", 8.8)),

                new Document("A superhero movie where Batman faces the Joker in Gotham City",
                        Map.of("title", "The Dark Knight", "genre", "Action", "year", 2008, "director", "Christopher Nolan", "rating", 9.0)),

                new Document("A team of explorers travel through a wormhole in space to ensure humanity's survival",
                        Map.of("title", "Interstellar", "genre", "Sci-Fi", "year", 2014, "director", "Christopher Nolan", "rating", 8.7)),

                new Document("The aging patriarch of a crime dynasty transfers control to his son",
                        Map.of("title", "The Godfather", "genre", "Crime", "year", 1972, "director", "Francis Ford Coppola", "rating", 9.2)),

                new Document("Two detectives hunt a serial killer who uses the seven deadly sins as motives",
                        Map.of("title", "Se7en", "genre", "Thriller", "year", 1995, "director", "David Fincher", "rating", 8.6)),

                new Document("A man with short-term memory loss uses notes and tattoos to find his wife's killer",
                        Map.of("title", "Memento", "genre", "Mystery", "year", 2000, "director", "Christopher Nolan", "rating", 8.4)),

                new Document("A young lion prince flees his kingdom after the murder of his father",
                        Map.of("title", "The Lion King", "genre", "Animation", "year", 1994, "director", "Roger Allers", "rating", 8.5)),

                new Document("A computer hacker learns the world he lives in is a simulated reality",
                        Map.of("title", "The Matrix", "genre", "Sci-Fi", "year", 1999, "director", "Lana Wachowski", "rating", 8.7)),

                new Document("A hobbit embarks on a journey with a wizard and dwarves to reclaim a mountain kingdom",
                        Map.of("title", "The Hobbit", "genre", "Fantasy", "year", 2012, "director", "Peter Jackson", "rating", 7.8)),

                new Document("A cowboy doll feels threatened by a new spaceman toy in his owner's room",
                        Map.of("title", "Toy Story", "genre", "Animation", "year", 1995, "director", "John Lasseter", "rating", 8.3))
        );
        return movies;
    }
}
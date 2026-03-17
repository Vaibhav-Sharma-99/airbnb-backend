package com.sharmavaibhav.airBnbApp.service;

import com.sharmavaibhav.airBnbApp.dto.JokeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiService {

    private final ChatClient chatClient;

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
}
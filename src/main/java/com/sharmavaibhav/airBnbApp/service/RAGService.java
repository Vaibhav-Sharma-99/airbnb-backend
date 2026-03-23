package com.sharmavaibhav.airBnbApp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RAGService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    @Value( "classpath:faq.pdf")
    Resource pdfFile;

    public void ingestPdfToVectorStore(){
        PagePdfDocumentReader reader = new PagePdfDocumentReader(pdfFile);
        List<Document> pages = reader.get();

        TokenTextSplitter tokenTextSplitter = TokenTextSplitter.builder()
                .withChunkSize(200)
                .build();

        List<Document> chunks = tokenTextSplitter.apply(pages);
        System.out.println("chunks got, now adding them to vector db");
        for(Document doc: chunks){
            try{
                vectorStore.add(List.of(doc));
            }
            catch(Exception e){
                System.out.println("Failed Chunk: ");
                System.out.println(doc.getText());
                e.printStackTrace();
            }
        }
//        vectorStore.add(chunks);
    }

    public String askRAGAi(String prompt){
        String template = returnPromptTemplate1(); // gettingt the system prompt

        //getting the matching chunks with user query/prompt
        var docs = vectorStore.similaritySearch(SearchRequest.builder()
                        .query(prompt)
                        .similarityThreshold(0.25)
                        .filterExpression("file_name == 'faq.pdf'")
                        .topK(4)
                .build());

        //converting the matching chunks from vector store to string
        var context = docs.stream()
                .map(Document:: getText)
                .collect(Collectors.joining("\n\n"));

        //concatenating the matching chunks as context to pass to chatClient
        PromptTemplate promptTemplate = new PromptTemplate(template);
        String stuffedPrompt = promptTemplate.render(Map.of("context", context));

        return chatClient.prompt()
                .system(stuffedPrompt)
                .user(prompt)
                .advisors()
                .call()
                .content();
    }

    public String returnPromptTemplate1(){
        return """
                You are an AI assistant called Cody
                
                Rules:
                - Use ONLY the information provided in the context
                - You MAY rephrase, summarize, and explain in natural language
                - Do NOT introduce new concepts or facts
                - If multiple context sections are relevant, combine them into a single explanation.
                - If the answer is not present, say "I don't know"
                
                Context:
                {context}
                
                Answer in a friendly, conversational tone.
                """;
    }
}

package com.example.assistant;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@SpringBootApplication
public class AssistantApplication {

    public static void main(String[] args) {
        SpringApplication.run(AssistantApplication.class, args);
    }

    @Bean
    QuestionAnswerAdvisor questionAnswerAdvisor(VectorStore vectorStore) {
        return QuestionAnswerAdvisor.builder(vectorStore).build();
    }

}


@Controller
@ResponseBody
class AssistantController {

    private final ChatClient ai;

    AssistantController(QuestionAnswerAdvisor qa,
                        DogRepository r,
                        VectorStore v,
                        ChatClient.Builder ai) {
        this.ai = ai
                .defaultAdvisors(qa)
                .defaultSystem("""
                        you are an AI powered assistant to help people adopt a dog from the adoptions agency named Pooch Palace 
                        with locations in Taipei, Utrecht, Seoul, Tokyo, Singapore, Paris, Mumbai, New Delhi, Barcelona, San Francisco, St. Louis, and London. 
                        Information about the dogs availables will be presented below. If there is no information, then return a polite response suggesting wes don't have any dogs available. If somebody asks you about animals, and there's no information in the context, then feel free to source the answer from other places. If somebody asks for a time to pick up the dog, don't ask other questions: simply provide a time by consulting the tools you have available.\s
                        """)
                .build();


        if (false) {
            for (var d : r.findAll()) {
                var dogument = new Document("id: %s, name: %s, description: %s".formatted(d.id(), d.name(), d.description()));
                v.add(List.of(dogument));
            }
        }
    }

    @GetMapping("/ask")
    DogAdoptionSuggestion ask(@RequestParam String question) {
        return this.ai
                .prompt(question)
                .call()
                .entity(DogAdoptionSuggestion.class);
    }

}

record DogAdoptionSuggestion(int dogId) {
}

// look mom, no Lombok!
record Dog(@Id int id, String name, String description, String owner) {
}

interface DogRepository extends ListCrudRepository<Dog, Integer> {
}

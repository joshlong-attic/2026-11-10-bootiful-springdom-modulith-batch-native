package com.example.adoptions.dogs;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.integration.core.GenericHandler;
import org.springframework.integration.core.GenericTransformer;
import org.springframework.integration.dsl.DirectChannelSpec;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.json.ObjectToJsonTransformer;
import org.springframework.messaging.MessageChannel;
import org.springframework.modulith.events.Externalized;

@Externalized(IntegrationConfiguration.DOGS_EVENTS)
public record DogAdoptedEvent(int dogId) {
}

@Configuration
class IntegrationConfiguration {

    static final String DOGS_EVENTS = "dogsEvents";

    @Bean
    IntegrationFlow inboundEvents(
            @Value("file://${HOME}/Desktop/dogs-events") Resource resource,
            @Qualifier(DOGS_EVENTS) MessageChannel channel) throws Exception{
        var files = Files.outboundAdapter(resource.getFile())
                .autoCreateDirectory(true);
        return IntegrationFlow
                .from(channel)
                .transform(new ObjectToJsonTransformer())
                .handle(files)
//                .handle((GenericHandler<DogAdoptedEvent>) (payload, headers) -> {
//                    headers.forEach((k, v) -> IO.println(k + '=' + v));
//                    IO.println("got a new dog adopted event! " + payload);
//                    return null;
//                })
                .get();
    }

    @Bean
    @Qualifier(DOGS_EVENTS)
    DirectChannelSpec dogsEvents() {
        return MessageChannels.direct();
    }

}
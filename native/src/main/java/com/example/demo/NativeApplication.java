package com.example.demo;

import org.jspecify.annotations.Nullable;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.nio.charset.Charset;

@SpringBootApplication
public class NativeApplication {

    public static void main(String[] args) {
        SpringApplication.run(NativeApplication.class, args);
    }

}


@Component
@ImportRuntimeHints(Runner.Hints.class)
class Runner implements ApplicationRunner {

    Runner(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    static class Hints implements RuntimeHintsRegistrar {

        @Override
        public void registerHints(RuntimeHints hints, @Nullable ClassLoader classLoader) {
            hints.resources().registerResource(RESOURCE);
            hints.reflection().registerType(Customer.class, MemberCategory.values());
        }
    }


    private static final Resource RESOURCE = new ClassPathResource("/message");

    private final JsonMapper jsonMapper;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        var customer = new Customer(1);
        this.jsonMapper.writer().writeValueAsString(customer);
        IO.println(RESOURCE.getContentAsString(Charset.defaultCharset()));
    }
}

record Customer(int id) {
}
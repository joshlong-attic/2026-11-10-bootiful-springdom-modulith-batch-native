package com.example.adoptions.cats;

import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

@Controller
@ResponseBody
class CatFactsController {

    private final CatsClient catsClient;

    private final AtomicInteger counter = new AtomicInteger(0);

    CatFactsController(CatsClient catsClient) {
        this.catsClient = catsClient;
    }

    @Retryable(maxRetries = 5, includes = IllegalStateException.class)
    @GetMapping("/cats")
    Collection<CatFact> facts() {

        if (this.counter.getAndIncrement() < 5) {
            IO.println("NO CATS!");
            throw new IllegalStateException("NO CATS!");
        }
        IO.println("CATS!");
        return this.catsClient.facts().facts();
    }
}

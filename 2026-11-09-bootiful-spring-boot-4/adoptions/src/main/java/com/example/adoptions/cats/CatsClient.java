package com.example.adoptions.cats;

import org.springframework.web.service.annotation.GetExchange;

interface CatsClient {

    @GetExchange("https://www.catfacts.net/api")
    CatFacts facts();
}

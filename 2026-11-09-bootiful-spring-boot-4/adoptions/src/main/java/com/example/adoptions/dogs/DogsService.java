package com.example.adoptions.dogs;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
@Transactional
class DogsService {

    private final DogRepository repository;
    private final ApplicationEventPublisher applicationEventPublisher;

    DogsService(DogRepository repository, ApplicationEventPublisher applicationEventPublisher) {
        this.repository = repository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    void adopt(int dogId, String owner) {
        this.repository.findById(dogId).ifPresent(dog -> {
            var adopted = this.repository.save(new Dog(dogId,
                    dog.name(), dog.description(), owner));
            this.applicationEventPublisher.publishEvent(
                    new DogAdoptedEvent(dogId));
            IO.println("adopted " + adopted);
        });

    }

    Collection<Dog> all() {
        return this.repository.findAll();
    }
}

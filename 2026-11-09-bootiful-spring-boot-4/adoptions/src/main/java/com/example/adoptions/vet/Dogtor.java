package com.example.adoptions.vet;

import com.example.adoptions.dogs.DogAdoptedEvent;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

@Service
class Dogtor {

    @ApplicationModuleListener
    void checkup(DogAdoptedEvent animalId) throws Exception {
        Thread.sleep(5000);
        IO.println("checking up on " + animalId);
    }
}

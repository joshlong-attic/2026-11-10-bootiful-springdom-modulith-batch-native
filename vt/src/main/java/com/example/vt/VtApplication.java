package com.example.vt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClient;

import java.util.concurrent.Executors;

@SpringBootApplication
public class VtApplication {

    public static void main(String[] args) {
        SpringApplication.run(VtApplication.class, args);


//        var thread = Thread.ofVirtual().unstarted(new Runnable() {
//            @Override
//            public void run() {
//                // IO...
//            }
//        }) ;
//
//        var executor = Executors.newVirtualThreadPerTaskExecutor();
//        executor.incomplete();

    }
}

// cora iberkleid: legend


@Controller
@ResponseBody
class ThreadsController {

    private final RestClient http;

    ThreadsController(RestClient.Builder http) {
        this.http = http.build();
    }

    @GetMapping("/threads")
    String threads() {
        var delta = System.currentTimeMillis();
        var threads = Thread.currentThread() + ":";
        var response = this.http
                .get()
                .uri("http://localhost:9000/delay/5")
                .retrieve()
                .body(String.class);
        var delta2 = System.currentTimeMillis();
        threads += Thread.currentThread() + "(" + (delta2 - delta) + ")";
        IO.println(threads);
        return response;
    }
}
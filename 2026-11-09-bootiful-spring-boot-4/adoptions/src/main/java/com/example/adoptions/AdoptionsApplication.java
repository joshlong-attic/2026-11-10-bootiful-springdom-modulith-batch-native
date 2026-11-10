package com.example.adoptions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.sql.DataSource;
import java.security.Principal;
import java.util.Map;

@SpringBootApplication
public class AdoptionsApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdoptionsApplication.class, args);
    }

}

@Controller
@ResponseBody
class SecureController {

    @GetMapping("/me")
    Map<String, String> me(Principal principal) {
        return Map.of("name", principal.getName());
    }
}
/*
@EnableMultiFactorAuthentication(authorities = {
        FactorGrantedAuthority.OTT_AUTHORITY,
        FactorGrantedAuthority.PASSWORD_AUTHORITY
})*/
@Configuration
class SecurityConfiguration {
/*
    @Bean
    Customizer<HttpSecurity> securityCustomizer() {
        return http -> http
                .webAuthn(a -> a
                        .allowedOrigins("http://localhost:8080")
                        .rpId("localhost")
                        .rpName("edj")
                )
                .oneTimeTokenLogin(ott -> ott
                        .tokenGenerationSuccessHandler((request, response, oneTimeToken) -> {
                            response.getWriter().println("you've got console mail!");
                            response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE);
                            IO.println(oneTimeToken.getUsername() + ", please go to http://localhost:8080/login/ott?token=" +
                                    oneTimeToken.getTokenValue());
                            // possession
                            // inherence
                            // knowledge
                        }));
    }*/

//    @Bean
//    SecurityFilterChain securityFilterChain(HttpSecurity security) {
//        return security.build();
//    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    JdbcUserDetailsManager userDetailsService(DataSource dataSource) {
        var u = new JdbcUserDetailsManager(dataSource);
        u.setEnableUpdatePassword(true);
        return u;
    }
}

// authentication: who is attempting the request
// authorization: what is the user allowed to do


// CustomerService
// DataSource
// CustomerService needs DataSource


//@Component
//


/*
@Component
class CatsClient {


    private final RestClient http;

    CatsClient(RestClient.Builder http) {
        this.http = http.build();
    }

    CatFacts facts() {
        return this.http
                .get()
                .uri("https://www.catfacts.net/api")
                .retrieve()
                .body(CatFacts.class);
    }
}

 */


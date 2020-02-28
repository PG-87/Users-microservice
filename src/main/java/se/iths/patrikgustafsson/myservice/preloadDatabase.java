package se.iths.patrikgustafsson.myservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@Slf4j
public class preloadDatabase {

    @Bean
    CommandLineRunner initDatabase(UsersRepository repository){
        return args -> {
            log.info("Preloading " + repository.save(new User(1L, "Kalle", "Carl-Henrik", "Trollhättan", 10000, true)));
            log.info("Preloading " + repository.save(new User(2L, "Nisse", "Nils", "Göteborg", 23000, false)));
        };
    }

    @Bean
    RestTemplate getRestTemplate(){
        return new RestTemplate();
    }
}

package au.com.sportsbet.movietickets;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class MovieTicketsApplication {
    static void main(String[] args) {
        SpringApplication.run(MovieTicketsApplication.class, args);
    }
}

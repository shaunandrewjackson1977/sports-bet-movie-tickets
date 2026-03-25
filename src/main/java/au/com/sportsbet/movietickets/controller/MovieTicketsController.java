package au.com.sportsbet.movietickets.controller;

import au.com.sportsbet.movietickets.model.request.MovieTransaction;
import au.com.sportsbet.movietickets.model.response.MovieTickets;
import au.com.sportsbet.movietickets.service.MovieTicketsService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class MovieTicketsController {
    private static final Logger logger = LoggerFactory.getLogger(MovieTicketsController.class);

    private MovieTicketsService movieTicketsService;

    @Autowired
    public MovieTicketsController(MovieTicketsService movieTicketsService) {
        this.movieTicketsService = movieTicketsService;
    }

    @PostMapping("/tickets")
    public ResponseEntity<MovieTickets> movieTickets(@RequestBody @Valid MovieTransaction movieTransaction) {
        logger.info("Movie Tickets request: {}", movieTransaction);
        var movieTickets = movieTicketsService.generateTickets(movieTransaction);
        return ResponseEntity.ok().body(movieTickets);
    }
}

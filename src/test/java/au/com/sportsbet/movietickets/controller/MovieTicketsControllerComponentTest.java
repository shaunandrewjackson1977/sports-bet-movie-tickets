package au.com.sportsbet.movietickets.controller;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static au.com.sportsbet.movietickets.FixturesHelper.readFixture;
import static au.com.sportsbet.movietickets.ModelFixturesHelper.newCustomer;
import static au.com.sportsbet.movietickets.ModelFixturesHelper.newMovieTransaction;

/**
 *   RestTestClient
 *
 *   - Fluent, reactive-style test client
 *   - With RANDOM_PORT it binds to a real embedded server and makes actual HTTP calls over the network
 *   - Full integration test — the entire application context is loaded
 *   - Slower but tests the full stack end-to-end
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
class MovieTicketsControllerComponentTest {
    @Autowired
    private RestTestClient restTestClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    class ExampleTestsFromSpec {
        // example 1 from the spec
        @Test
        void shouldReturnExpectedForTwoChildrenOneSenior() throws Exception {
            var requestContent = objectMapper.writeValueAsString(
                    newMovieTransaction(
                            1L,
                            List.of(
                                    newCustomer("John Smith", 70),
                                    newCustomer("Jane Doe", 5),
                                    newCustomer("Bob Doe", 5)
                            )
                    )
            );

            sentPostExpectOkWithPayload(
                    requestContent,
                    readFixture("movie_tickets_one_senior_two_children.json")
            );
        }

        // example 2 from the spec
        @Test
        void shouldReturnExpectedForOneAdultThreeChildrenOneTeen() throws Exception {
            var requestContent = objectMapper.writeValueAsString(
                    newMovieTransaction(
                            2L,
                            List.of(
                                    newCustomer("Billy Kidd", 36),
                                    newCustomer("Zoe Daniels", 3),
                                    newCustomer("George White", 8),
                                    newCustomer("Tommy Anderson", 9),
                                    newCustomer("Joe Smith", 17)
                            )
                    )
            );

            sentPostExpectOkWithPayload(
                    requestContent,
                    readFixture("movie_tickets_one_adult_one_teen_three_children.json")
            );
        }

        // example 3 from the spec
        @Test
        void shouldReturnExpectedForOneAdultOneChildOneSeniorOneTeen() throws Exception {
            var requestContent = objectMapper.writeValueAsString(
                    newMovieTransaction(
                            3L,
                            List.of(
                                    newCustomer("Jesse James", 36),
                                    newCustomer("Daniel Anderson", 95),
                                    newCustomer("Mary Jones", 15),
                                    newCustomer("Michelle Parker", 10)
                            )
                    )
            );

            sentPostExpectOkWithPayload(
                    requestContent,
                    readFixture("movie_tickets_one_adult_one_child_one_senior_one_teen.json")
            );
        }
    }

    private void sentPostExpectOkWithPayload(String requestContent, String expectedContent) {
        restTestClient.post()
                .uri("/api/v1/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestContent)
                .exchangeSuccessfully()
                .expectStatus().isOk()
                .expectBody().json(expectedContent);
    }
}
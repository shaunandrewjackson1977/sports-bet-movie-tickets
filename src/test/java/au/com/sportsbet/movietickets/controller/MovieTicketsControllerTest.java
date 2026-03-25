package au.com.sportsbet.movietickets.controller;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static au.com.sportsbet.movietickets.FixturesHelper.readFixture;
import static au.com.sportsbet.movietickets.ModelFixturesHelper.newCustomer;
import static au.com.sportsbet.movietickets.ModelFixturesHelper.newMovieTransaction;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 *   MockMvc
 *
 *   - Does not start a real server — it mocks the servlet layer
 *   - Uses @WebMvcTest (slice test) or @SpringBootTest without a real port
 *   - Faster, more isolated — only loads the web layer (controllers, filters, etc.)
 *   - Synchronous API
 *   - Can't test anything below the servlet layer (e.g. actual network, port binding)
 */
@SpringBootTest
@AutoConfigureMockMvc
class MovieTicketsControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    class ValidationTests {
        @Test
        void shouldReturnBadRequestResponseWhenTransactionIdIsLessThanMin() throws Exception {
            var request = newMovieTransaction(0L, List.of(newCustomer()));
            var jsonRequest = objectMapper.writeValueAsString(request);
            sendPostExpectBadRequestWithMessage(jsonRequest, "transactionId: must be greater than or equal to 1");
        }

        @Test
        void shouldReturnBadRequestResponseWhenCustomerListEmpty() throws Exception {
            var request = newMovieTransaction(1L, List.of());
            var jsonRequest = objectMapper.writeValueAsString(request);
            sendPostExpectBadRequestWithMessage(jsonRequest, "customers: must not be empty");
        }

        @Test
        void shouldReturnBadRequestResponseWhenCustomerNameBlank() throws Exception {
            var request = newMovieTransaction(1L, List.of(newCustomer(" ", 30)));
            var jsonRequest = objectMapper.writeValueAsString(request);
            sendPostExpectBadRequestWithMessage(jsonRequest, "customers[0].name: must not be blank");
        }

        @Test
        void shouldReturnBadRequestResponseWhenCustomerAgeBelowMin() throws Exception {
            var request = newMovieTransaction(1L, List.of(newCustomer("Joe Smith", 0)));
            var jsonRequest = objectMapper.writeValueAsString(request);
            sendPostExpectBadRequestWithMessage(jsonRequest, "customers[0].age: must be greater than or equal to 1");
        }

        @Test
        void shouldReturnBadRequestResponseWhenCustomerAgeAboveMax() throws Exception {
            var request = newMovieTransaction(1L, List.of(newCustomer("Joe Smith", 101)));
            var jsonRequest = objectMapper.writeValueAsString(request);
            sendPostExpectBadRequestWithMessage(jsonRequest, "customers[0].age: must be less than or equal to 100");
        }
    }

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
                                    newCustomer("Bob Doe", 6)
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

    private void sendPostExpectBadRequestWithMessage(String requestContent, String expectedMessage) throws Exception {
        mvc.perform(
                post("/api/v1/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestContent)
        ).andExpect(
                status().isBadRequest()
        ).andExpect(
                jsonPath("$.errors[0]")
                        .value(expectedMessage)
        );
    }

    private void sentPostExpectOkWithPayload(String requestContent, String expectedContent) throws Exception {
        mvc.perform(
                post("/api/v1/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestContent)
        ).andExpect(
                status().isOk()
        ).andExpect(
                content().json(expectedContent)
        );
    }
}
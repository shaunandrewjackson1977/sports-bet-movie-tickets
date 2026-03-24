package au.com.sportsbet.movietickets.model.response;

import au.com.sportsbet.movietickets.model.JsonSerializationTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static au.com.sportsbet.movietickets.ModelFixturesHelper.newMovieTickets;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

class MovieTicketsTest extends JsonSerializationTest {
    private final MovieTickets movieTickets = newMovieTickets();

    @Override
    protected String defaultFixtureFile() {
        return "movie_tickets.json";
    }

    @Test
    public void shouldSerializeToJson() throws Exception {
        var expectedJson = readFixtureFile(defaultFixtureFile());
        var actualJson = serializeJavaToJsonString(movieTickets);
        assertEquals(expectedJson, actualJson, false);
    }

    @Test
    public void shouldDeserializeToJava() throws IOException {
        var actualJson = readFixtureFile(defaultFixtureFile());
        var actualMovieTickets = deserializeJsonStringToJava(actualJson, MovieTickets.class);
        assertThat(actualMovieTickets).isEqualTo(movieTickets);
    }
}
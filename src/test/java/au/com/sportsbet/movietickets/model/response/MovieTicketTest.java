package au.com.sportsbet.movietickets.model.response;

import au.com.sportsbet.movietickets.model.JsonSerializationTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static au.com.sportsbet.movietickets.ModelFixturesHelper.newMovieTicket;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

class MovieTicketTest extends JsonSerializationTest {
    private final MovieTicket movieTicket = newMovieTicket();

    @Override
    protected String defaultFixtureFile() {
        return "movie_ticket.json";
    }

    @Test
    public void shouldSerializeToJson() throws Exception {
        var expectedJson = readFixtureFile(defaultFixtureFile());
        var actualJson = serializeJavaToJsonString(movieTicket);
        assertEquals(expectedJson, actualJson, true);
    }

    @Test
    public void shouldDeserializeToJava() throws IOException {
        var actualJson = readFixtureFile(defaultFixtureFile());
        var actualMovieTicket = deserializeJsonStringToJava(actualJson, MovieTicket.class);
        assertThat(actualMovieTicket).isEqualTo(movieTicket);
    }
}
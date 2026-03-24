package au.com.sportsbet.movietickets.model.request;

import au.com.sportsbet.movietickets.model.JsonSerializationTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static au.com.sportsbet.movietickets.ModelFixturesHelper.newMovieTransaction;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

class MovieTransactionTest extends JsonSerializationTest {
    private final MovieTransaction movieTransaction = newMovieTransaction();

    @Override
    protected String defaultFixtureFile() {
        return "movie_transaction.json";
    }

    @Test
    public void shouldSerializeToJson() throws Exception {
        var expectedJson = readFixtureFile(defaultFixtureFile());
        var actualJson = serializeJavaToJsonString(movieTransaction);
        assertEquals(expectedJson, actualJson, true);
    }

    @Test
    public void shouldDeserializeToJava() throws IOException {
        var actualJson = readFixtureFile(defaultFixtureFile());
        var actualMovieTransaction = deserializeJsonStringToJava(actualJson, MovieTransaction.class);
        assertThat(actualMovieTransaction).isEqualTo(movieTransaction);
    }
}
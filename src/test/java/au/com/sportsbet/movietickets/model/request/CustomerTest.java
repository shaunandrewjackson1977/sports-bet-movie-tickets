package au.com.sportsbet.movietickets.model.request;

import au.com.sportsbet.movietickets.model.JsonSerializationTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static au.com.sportsbet.movietickets.ModelFixturesHelper.newCustomer;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

class CustomerTest extends JsonSerializationTest {
    private final Customer customer = newCustomer();

    @Override
    protected String defaultFixtureFile() {
        return "customer.json";
    }

    @Test
    public void shouldSerializeToJson() throws Exception {
        var expectedJson = readFixtureFile(defaultFixtureFile());
        var actualJson = serializeJavaToJsonString(customer);
        assertEquals(expectedJson, actualJson, true);
    }

    @Test
    public void shouldDeserializeToJava() throws IOException {
        var actualJson = readFixtureFile(defaultFixtureFile());
        var actualCustomer = deserializeJsonStringToJava(actualJson, Customer.class);
        assertThat(actualCustomer).isEqualTo(customer);
    }
}
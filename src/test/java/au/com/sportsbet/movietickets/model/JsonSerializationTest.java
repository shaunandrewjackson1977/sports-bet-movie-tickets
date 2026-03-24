package au.com.sportsbet.movietickets.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

import static au.com.sportsbet.movietickets.FixturesHelper.readFixture;

@JsonTest
public abstract class JsonSerializationTest {
    @Autowired
    protected ObjectMapper objectMapper;

    protected abstract String defaultFixtureFile();

    protected String serializeJavaToJsonString(Object obj) {
        return objectMapper.writeValueAsString(obj);
    }

    protected Object deserializeJsonStringToJava(String json, Class<?> clazz) {
        return objectMapper.readValue(json, clazz);
    }

    protected static String readFixtureFile(String name) throws IOException {
        return readFixture(name);
    }
}

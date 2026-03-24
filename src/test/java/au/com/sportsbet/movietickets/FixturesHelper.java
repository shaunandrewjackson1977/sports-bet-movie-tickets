package au.com.sportsbet.movietickets;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class FixturesHelper {
    public static String readFixture(String filename) throws IOException {
        try (var stream = FixturesHelper.class.getResourceAsStream("/fixtures/" + filename)) {
            assert stream != null;
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}

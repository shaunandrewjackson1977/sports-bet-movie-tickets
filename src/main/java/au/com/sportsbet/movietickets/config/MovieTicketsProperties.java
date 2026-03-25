package au.com.sportsbet.movietickets.config;

import au.com.sportsbet.movietickets.model.response.TicketType;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "movietickets")
public record MovieTicketsProperties(Pricing pricing) {

    public record Pricing(
        int childrenGroupDiscountThreshold,
        double childrenGroupDiscountRate,
        List<AgeRange> ageRanges,
        Map<TicketType, Price> ticketPrices
    ) {}

    public record AgeRange(int min, int max, TicketType ticketType) {}

    public record Price(BigDecimal amount, String currency) {}
}

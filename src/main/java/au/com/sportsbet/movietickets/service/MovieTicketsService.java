package au.com.sportsbet.movietickets.service;

import au.com.sportsbet.movietickets.model.request.MovieTransaction;
import au.com.sportsbet.movietickets.model.response.MovieTicket;
import au.com.sportsbet.movietickets.model.response.MovieTickets;
import au.com.sportsbet.movietickets.model.response.TicketType;
import org.javamoney.moneta.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.money.MonetaryAmount;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MovieTicketsService {
    private static final Logger logger = LoggerFactory.getLogger(MovieTicketsService.class);

    private static final int CHILDREN_GROUP_DISCOUNT_THRESHOLD = 3;
    private static final double CHILDREN_GROUP_DISCOUNT_RATE = 0.75d;

    private static final Map<Range, TicketType> ticketTypes = new LinkedHashMap<>(
            Map.of(
                new Range(1, 10), TicketType.CHILDREN,
                new Range(11, 17), TicketType.TEEN,
                new Range(18, 64), TicketType.ADULT,
                new Range(65, 100), TicketType.SENIOR
            )
    );

    private static final Map<TicketType, MonetaryAmount> ticketPrices = Map.of(
            TicketType.CHILDREN, Money.of(5, "AUD"),
            TicketType.TEEN, Money.of(12, "AUD"),
            TicketType.ADULT, Money.of(25, "AUD"),
            TicketType.SENIOR, Money.of(17.5d, "AUD")
    );

    public MovieTickets generateTickets(MovieTransaction movieTransaction) {
        logger.debug("Processing transaction id={} with {} customer(s)",
                movieTransaction.transactionId(), movieTransaction.customers().size());

        var countsByType = movieTransaction.customers().stream()
                .collect(Collectors.groupingBy(
                        customer -> resolveTicketType(customer.age()),
                        Collectors.counting()
                ));

        var ticketTypeSummaries = new HashMap<TicketType, TicketTypeSummary>();
        countsByType.forEach((ticketType, count) -> {
            MonetaryAmount totalCost = ticketPrices.get(ticketType).multiply(count);
            ticketTypeSummaries.put(ticketType, new TicketTypeSummary(count.intValue(), totalCost));
        });

        // If 3 or more children's tickets are in the transaction, apply a 25% group discount
        // to the total cost of all children's tickets.
        ticketTypeSummaries.computeIfPresent(TicketType.CHILDREN, (_, existing) ->
                applyChildrenGroupDiscount(existing, movieTransaction.transactionId())
        );

        return new MovieTickets(
                movieTransaction.transactionId(),
                toSortedMovieTicketList(ticketTypeSummaries),
                toTotalCostMoney(ticketTypeSummaries)
        );
    }

    private TicketType resolveTicketType(int age) {
        return ticketTypes.entrySet().stream()
                .filter(e -> e.getKey().contains(age))
                .findFirst()
                .orElseThrow(() -> {
                    logger.warn("No ticket type found for age={}", age);
                    return new IllegalArgumentException("No ticket type for age: " + age);
                })
                .getValue();
    }

    private TicketTypeSummary applyChildrenGroupDiscount(TicketTypeSummary existing, long transactionId) {
        if (existing.quantity() >= CHILDREN_GROUP_DISCOUNT_THRESHOLD) {
            logger.debug("Applying group discount to children's tickets for transaction id={}", transactionId);
            return new TicketTypeSummary(existing.quantity(), existing.totalCost().multiply(CHILDREN_GROUP_DISCOUNT_RATE));
        }
        return existing;
    }


    private MonetaryAmount toTotalCostMoney(Map<TicketType, TicketTypeSummary> summaries) {
        return summaries.values().stream()
                .map(TicketTypeSummary::totalCost)
                .reduce(Money.of(0, "AUD"), MonetaryAmount::add);
    }

    private List<MovieTicket> toSortedMovieTicketList(Map<TicketType, TicketTypeSummary> summaries) {
        return summaries.entrySet().stream()
                .map( e -> new MovieTicket(e.getKey(), e.getValue().quantity(), e.getValue().totalCost()))
                .sorted(Comparator.comparing(movieTicket -> movieTicket.ticketType().getDisplayName()))
                .collect(Collectors.toList());
    }
}

package au.com.sportsbet.movietickets.model.response;

import javax.money.MonetaryAmount;
import java.util.Set;

public record MovieTickets(Long transactionId, Set<MovieTicket> tickets, MonetaryAmount totalCost) {
}

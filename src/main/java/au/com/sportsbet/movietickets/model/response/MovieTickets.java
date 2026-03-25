package au.com.sportsbet.movietickets.model.response;

import javax.money.MonetaryAmount;
import java.util.List;

public record MovieTickets(long transactionId, List<MovieTicket> tickets, MonetaryAmount totalCost) {
}

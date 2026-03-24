package au.com.sportsbet.movietickets.model.response;

import javax.money.MonetaryAmount;

public record MovieTicket(TicketType ticketType, int quantity, MonetaryAmount totalCost) {
}

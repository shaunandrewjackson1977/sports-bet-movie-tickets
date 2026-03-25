package au.com.sportsbet.movietickets.service;

import javax.money.MonetaryAmount;

record TicketTypeSummary(int quantity, MonetaryAmount totalCost) {
}

package au.com.sportsbet.movietickets;

import au.com.sportsbet.movietickets.model.request.Customer;
import au.com.sportsbet.movietickets.model.request.MovieTransaction;
import au.com.sportsbet.movietickets.model.response.MovieTicket;
import au.com.sportsbet.movietickets.model.response.MovieTickets;
import au.com.sportsbet.movietickets.model.response.TicketType;

import javax.money.MonetaryAmount;
import java.util.List;
import java.util.Set;

import static au.com.sportsbet.movietickets.MonetaryAmountHelper.audMonetaryAmount;

public class ModelFixturesHelper {
    // request model helpers
    public static Customer newCustomer() {
        return newCustomer("Joe Smith", 30);
    }

    public static Customer newCustomer(String name, int age) {
        return new Customer(name, age);
    }

    public static MovieTransaction newMovieTransaction() {
        return new MovieTransaction(1L, List.of(
                newCustomer(),
                newCustomer("Jenny Smith", 15)
        ));
    }

    public static MovieTransaction newMovieTransaction(long transactionId, List<Customer> customers) {
        return new MovieTransaction(transactionId, customers);
    }

    // response model helpers

    public static MovieTicket newMovieTicket() {
        return newMovieTicket(TicketType.ADULT, 1, audMonetaryAmount(25));
    }

    public static MovieTicket newMovieTicket(TicketType ticketType, int quantity, MonetaryAmount totalCost) {
        return new MovieTicket(ticketType, quantity, totalCost);
    }

    public static MovieTickets newMovieTickets() {
        return newMovieTickets(
                1L,
                Set.of(
                        new MovieTicket(
                                TicketType.ADULT,
                                1,
                                audMonetaryAmount(25)
                        ),
                        new MovieTicket(
                                TicketType.CHILDREN,
                                1,
                                audMonetaryAmount(5)
                        )
                ),
                audMonetaryAmount(30));
    }

    public static MovieTickets newMovieTickets(long transactionId, Set<MovieTicket> tickets, MonetaryAmount totalCost) {
        return new MovieTickets(transactionId, tickets, totalCost);
    }
}

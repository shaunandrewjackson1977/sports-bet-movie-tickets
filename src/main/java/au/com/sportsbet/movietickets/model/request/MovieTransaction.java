package au.com.sportsbet.movietickets.model.request;

import java.util.List;

public record MovieTransaction(Long transactionId, List<Customer> customers) {
}

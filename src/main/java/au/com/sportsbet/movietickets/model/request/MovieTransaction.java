package au.com.sportsbet.movietickets.model.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record MovieTransaction(@Min(1) long transactionId, @NotEmpty @Valid List<Customer> customers) {
}

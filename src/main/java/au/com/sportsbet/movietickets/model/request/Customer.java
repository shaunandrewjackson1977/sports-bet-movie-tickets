package au.com.sportsbet.movietickets.model.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record Customer(@NotBlank String name, @Min(1) @Max(100) int age) {
}

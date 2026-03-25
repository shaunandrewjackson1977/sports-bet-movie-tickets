package au.com.sportsbet.movietickets.service;

import au.com.sportsbet.movietickets.config.MovieTicketsProperties;
import au.com.sportsbet.movietickets.model.request.MovieTransaction;
import au.com.sportsbet.movietickets.model.response.MovieTicket;
import au.com.sportsbet.movietickets.model.response.MovieTickets;
import au.com.sportsbet.movietickets.model.response.TicketType;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static au.com.sportsbet.movietickets.ModelFixturesHelper.newCustomer;
import static au.com.sportsbet.movietickets.ModelFixturesHelper.newMovieTicket;
import static au.com.sportsbet.movietickets.ModelFixturesHelper.newMovieTransaction;
import static org.assertj.core.api.Assertions.assertThat;

class MovieTicketsServiceTest {
    private static final MovieTicketsProperties TEST_PROPERTIES = new MovieTicketsProperties(
        new MovieTicketsProperties.Pricing(
            3,
            0.75,
            List.of(
                new MovieTicketsProperties.AgeRange(1, 10, TicketType.CHILDREN),
                new MovieTicketsProperties.AgeRange(11, 17, TicketType.TEEN),
                new MovieTicketsProperties.AgeRange(18, 64, TicketType.ADULT),
                new MovieTicketsProperties.AgeRange(65, 100, TicketType.SENIOR)
            ),
            Map.of(
                TicketType.CHILDREN, new MovieTicketsProperties.Price(new BigDecimal("5"), "AUD"),
                TicketType.TEEN,     new MovieTicketsProperties.Price(new BigDecimal("12"), "AUD"),
                TicketType.ADULT,    new MovieTicketsProperties.Price(new BigDecimal("25"), "AUD"),
                TicketType.SENIOR,   new MovieTicketsProperties.Price(new BigDecimal("17.5"), "AUD")
            )
        )
    );

    private final MovieTicketsService movieTicketsService = new MovieTicketsService(TEST_PROPERTIES);

    /*
     * Unlikely scenario tests but these focus on how the service functions when there are no customers
     * in the MovieTickets object
     */
    @Nested
    class NoTicketScenarioTests {
        private MovieTickets objectUnderTest;

        @BeforeEach
        public void setUp() {
            var movieTransaction = newMovieTransaction(1L, List.of());
            objectUnderTest = movieTicketsService.generateTickets(movieTransaction);
        }

        @Test
        void shouldHaveExpectedTransactionId() {
            assertTransactionId(objectUnderTest, 1L);
        }

        @Test
        void shouldHaveEmptyMovieTicketsSet() {
            assertThat(objectUnderTest.tickets()).isEmpty();
        }

        @Test
        void shouldHaveZeroTotalCost() {
            assertTotalCost(objectUnderTest, Money.of(0, "AUD"));
        }
    }

    @Nested
    class SingleTicketScenarioTests {
        /*
         * These focus on how the service functions when a customer in the adult age range (18-64) is included in the
         * MovieTickets object.
         */
        @Nested
        class AdultTicketTests {
            final MovieTransaction movieTransaction = newMovieTransaction(1L, List.of(newCustomer()));

            private MovieTickets objectUnderTest;

            @BeforeEach
            void setUp() {
                objectUnderTest = movieTicketsService.generateTickets(movieTransaction);
            }

            @Test
            public void shouldCorrectlyCalculateMovieTicketTypeQuantityAndCost() {
                assertMovieTickets(objectUnderTest, newMovieTicket(TicketType.ADULT, 1, Money.of(25, "AUD")));
            }

            @Test
            public void shouldCorrectlyCalculateTotalCost() {
                assertTotalCost(objectUnderTest, Money.of(25, "AUD"));
            }

            @ParameterizedTest
            @MethodSource("provideAgesInAdultRangeOf18To64")
            public void shouldRespectTicketTypeAgeRange(int adultAge) {
                var movieTransaction = newMovieTransaction(1, List.of(newCustomer(adultAge)));
                objectUnderTest = movieTicketsService.generateTickets(movieTransaction);
                assertMovieTickets(objectUnderTest, newMovieTicket(TicketType.ADULT, 1, Money.of(25, "AUD")));
            }

            static Stream<Arguments> provideAgesInAdultRangeOf18To64() {
                return Stream.of(
                        Arguments.of(18),
                        Arguments.of(35),
                        Arguments.of(41),
                        Arguments.of(57),
                        Arguments.of(64)
                );
            }
        }

        /*
         * These focus on how the service functions when a customer in the seniors age range (65+) is included in the
         * MovieTickets object.
         */
        @Nested
        class SeniorTicketTests {
            final MovieTransaction movieTransaction = newMovieTransaction(1L, List.of(newCustomer("Martha Jones", 75)));

            private MovieTickets objectUnderTest;

            @BeforeEach
            void setUp() {
                objectUnderTest = movieTicketsService.generateTickets(movieTransaction);
            }

            @Test
            public void shouldCorrectlyCalculateMovieTicketTypeQuantityAndCost() {
                assertMovieTickets(objectUnderTest, newMovieTicket(TicketType.SENIOR, 1, Money.of(17.5d, "AUD")));
            }

            @Test
            public void shouldCorrectlyCalculateTotalCost() {
                assertTotalCost(objectUnderTest, Money.of(17.5d, "AUD"));
            }

            @ParameterizedTest
            @MethodSource("provideAgesInSeniorRangeOf65To100")
            public void shouldRespectTicketTypeAgeRange(int seniorAge) {
                var movieTransaction = newMovieTransaction(1, List.of(newCustomer(seniorAge)));
                objectUnderTest = movieTicketsService.generateTickets(movieTransaction);
                assertMovieTickets(objectUnderTest, newMovieTicket(TicketType.SENIOR, 1, Money.of(17.5, "AUD")));
            }

            static Stream<Arguments> provideAgesInSeniorRangeOf65To100() {
                return Stream.of(
                        Arguments.of(65),
                        Arguments.of(73),
                        Arguments.of(88),
                        Arguments.of(91),
                        Arguments.of(100)
                );
            }
        }

        /*
         * These focus on how the service functions when a customer in the teen age range (11-17) is included in the
         * MovieTickets object.
         */
        @Nested
        class TeenTicketTests {
            final MovieTransaction movieTransaction = newMovieTransaction(1L, List.of(newCustomer("Chloe Debois", 16)));

            private MovieTickets objectUnderTest;

            @BeforeEach
            void setUp() {
                objectUnderTest = movieTicketsService.generateTickets(movieTransaction);
            }

            @Test
            public void shouldCorrectlyCalculateMovieTicketTypeQuantityAndCost() {
                assertMovieTickets(objectUnderTest, newMovieTicket(TicketType.TEEN, 1, Money.of(12, "AUD")));
            }

            @Test
            public void shouldCorrectlyCalculateTotalCost() {
                assertTotalCost(objectUnderTest, Money.of(12, "AUD"));
            }

            @ParameterizedTest
            @MethodSource("provideAgesInTeenRangeOf11To17")
            public void shouldRespectTicketTypeAgeRange(int teenAge) {
                var movieTransaction = newMovieTransaction(1, List.of(newCustomer(teenAge)));
                objectUnderTest = movieTicketsService.generateTickets(movieTransaction);
                assertMovieTickets(objectUnderTest, newMovieTicket(TicketType.TEEN, 1, Money.of(12, "AUD")));
            }

            static Stream<Arguments> provideAgesInTeenRangeOf11To17() {
                return Stream.of(
                        Arguments.of(11),
                        Arguments.of(12),
                        Arguments.of(14),
                        Arguments.of(16),
                        Arguments.of(17)
                );
            }
        }

        /*
         * These focus on how the service functions when a customer in the child age range (< 11) is included in the
         * MovieTickets object.
         */
        @Nested
        class ChildTicketTests {
            final MovieTransaction movieTransaction = newMovieTransaction(1L, List.of(newCustomer("Jack Sparrow", 8)));

            private MovieTickets objectUnderTest;

            @BeforeEach
            void setUp() {
                objectUnderTest = movieTicketsService.generateTickets(movieTransaction);
            }

            @Test
            public void shouldCorrectlyCalculateMovieTicketTypeQuantityAndCost() {
                assertMovieTickets(objectUnderTest, newMovieTicket(TicketType.CHILDREN, 1, Money.of(5, "AUD")));
            }

            @Test
            public void shouldCorrectlyCalculateTotalCost() {
                assertTotalCost(objectUnderTest, Money.of(5, "AUD"));
            }

            @ParameterizedTest
            @MethodSource("provideAgesInChildRangeOfUnder11")
            public void shouldRespectTicketTypeAgeRange(int childAge) {
                var movieTransaction = newMovieTransaction(1, List.of(newCustomer(childAge)));
                objectUnderTest = movieTicketsService.generateTickets(movieTransaction);
                assertMovieTickets(objectUnderTest, newMovieTicket(TicketType.CHILDREN, 1, Money.of(5, "AUD")));
            }

            static Stream<Arguments> provideAgesInChildRangeOfUnder11() {
                return Stream.of(
                        Arguments.of(1),
                        Arguments.of(3),
                        Arguments.of(5),
                        Arguments.of(7),
                        Arguments.of(10)
                );
            }
        }
    }

    @Nested
    class MultiTicketScenarioTests {
        @Test
        void shouldCorrectlyCalculateTicketAndTotalCostsForSingleAdultAndChild() {
            var customers = List.of(newCustomer(), (newCustomer("Jack Sparrow", 8)));
            var movieTransaction = newMovieTransaction(1L, customers);
            assertThat(movieTicketsService.generateTickets(movieTransaction)).satisfies(movieTickets -> {
                assertThat(movieTickets.transactionId()).isEqualTo(1);
                assertThat(movieTickets.tickets()).containsOnly(
                        newMovieTicket(TicketType.ADULT, 1, Money.of(25, "AUD")),
                        newMovieTicket(TicketType.CHILDREN, 1, Money.of(5, "AUD"))
                );
                assertThat(movieTickets.totalCost()).isEqualTo(Money.of(30, "AUD"));
            });
        }

        @Test
        void shouldCorrectlyCalculateTicketAndTotalCostsForTwoAdultsAndTwoChildren() {
            var customers = List.of(
                    newCustomer("John Sparrow", 45),
                    newCustomer("Jacinta Sparrow", 44),
                    newCustomer("Jack Sparrow", 8),
                    newCustomer("Julie Sparrow", 5)
            );
            var movieTransaction = newMovieTransaction(1L, customers);
            assertThat(movieTicketsService.generateTickets(movieTransaction)).satisfies(movieTickets -> {
                assertThat(movieTickets.transactionId()).isEqualTo(1);
                assertThat(movieTickets.tickets()).containsOnly(
                        newMovieTicket(TicketType.ADULT, 2, Money.of(50, "AUD")),
                        newMovieTicket(TicketType.CHILDREN, 2, Money.of(10, "AUD"))
                );
                assertThat(movieTickets.totalCost()).isEqualTo(Money.of(60, "AUD"));
            });
        }

        @Test
        void shouldCorrectlyCalculateTicketAndTotalCostsForTwoAdultsOneTeenAndTwoChildren() {
            var customers = List.of(
                    newCustomer("John Sparrow", 45),
                    newCustomer("Jacinta Sparrow", 44),
                    newCustomer("Jacob Sparrow", 15),
                    newCustomer("Jack Sparrow", 8),
                    newCustomer("Julie Sparrow", 5)
            );
            var movieTransaction = newMovieTransaction(1L, customers);
            assertThat(movieTicketsService.generateTickets(movieTransaction)).satisfies(movieTickets -> {
                assertThat(movieTickets.transactionId()).isEqualTo(1);
                assertThat(movieTickets.tickets()).containsOnly(
                        newMovieTicket(TicketType.ADULT, 2, Money.of(50, "AUD")),
                        newMovieTicket(TicketType.TEEN, 1, Money.of(12, "AUD")),
                        newMovieTicket(TicketType.CHILDREN, 2, Money.of(10, "AUD"))
                );
                assertThat(movieTickets.totalCost()).isEqualTo(Money.of(72, "AUD"));
            });
        }
    }

    @Nested
    class MultiTicketScenariosWithDiscountsTests {
        @Test
        void shouldCorrectlyCalculateTicketAndTotalCostsForTwoSeniorsAndThreeChildren() {
            var customers = List.of(
                    newCustomer("Robert Sparrow", 75),
                    newCustomer("Margaret Sparrow", 71),
                    newCustomer("Jack Sparrow", 8),
                    newCustomer("Julie Sparrow", 5),
                    newCustomer("Johanna Sparrow", 3)
            );
            var movieTransaction = newMovieTransaction(1L, customers);
            assertThat(movieTicketsService.generateTickets(movieTransaction)).satisfies(movieTickets -> {
                assertThat(movieTickets.transactionId()).isEqualTo(1);
                assertThat(movieTickets.tickets()).containsOnly(
                        newMovieTicket(TicketType.SENIOR, 2, Money.of(35, "AUD")),
                        newMovieTicket(TicketType.CHILDREN, 3, Money.of(11.25d, "AUD"))
                );
                assertThat(movieTickets.totalCost()).isEqualTo(Money.of(46.25d, "AUD"));
            });
        }

        @Test
        void shouldCorrectlyCalculateTicketAndTotalCostsForTwoSeniorsTwoAdultsOneTeenAndFourChildren() {
            var customers = List.of(
                    newCustomer("Robert Sparrow", 75),
                    newCustomer("Margaret Sparrow", 71),
                    newCustomer("John Sparrow", 45),
                    newCustomer("Jacinta Sparrow", 44),
                    newCustomer("Jacob Sparrow", 15),
                    newCustomer("Jason Sparrow", 10),
                    newCustomer("Jack Sparrow", 8),
                    newCustomer("Julie Sparrow", 5),
                    newCustomer("Johanna Sparrow", 3)
            );
            var movieTransaction = newMovieTransaction(1L, customers);
            assertThat(movieTicketsService.generateTickets(movieTransaction)).satisfies(movieTickets -> {
                assertThat(movieTickets.transactionId()).isEqualTo(1);
                assertThat(movieTickets.tickets()).containsOnly(
                        newMovieTicket(TicketType.SENIOR, 2, Money.of(35, "AUD")),
                        newMovieTicket(TicketType.ADULT, 2, Money.of(50, "AUD")),
                        newMovieTicket(TicketType.TEEN, 1, Money.of(12, "AUD")),
                        newMovieTicket(TicketType.CHILDREN, 4, Money.of(15d, "AUD"))
                );
                assertThat(movieTickets.totalCost()).isEqualTo(Money.of(112d, "AUD"));
            });
        }
    }

    // assertion helpers

    private static void assertTransactionId(MovieTickets movieTickets, long expectedTransactionId) {
        assertThat(movieTickets.transactionId()).isEqualTo(expectedTransactionId);
    }

    private static void assertMovieTickets(MovieTickets movieTickets, MovieTicket... expectedMovieTickets) {
        assertThat(movieTickets.tickets()).containsExactly(expectedMovieTickets);
    }

    private static void assertTotalCost(MovieTickets movieTickets, Money expectedTotalCost) {
        assertThat(movieTickets.totalCost()).isEqualTo(expectedTotalCost);
    }
}
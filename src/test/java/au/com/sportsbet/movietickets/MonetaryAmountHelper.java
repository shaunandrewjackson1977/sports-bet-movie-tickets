package au.com.sportsbet.movietickets;

import javax.money.Monetary;
import javax.money.MonetaryAmount;

public class MonetaryAmountHelper {
    public static MonetaryAmount audMonetaryAmount(long number) {
        return newMonetaryAmount("AUD", number);
    }

    public static MonetaryAmount newMonetaryAmount(String currencyUnit, long number) {
        return Monetary.getDefaultAmountFactory()
                .setCurrency(currencyUnit)
                .setNumber(number)
                .create();
    }

    public static MonetaryAmount newMonetaryAmount(String currencyUnit, double number) {
        return Monetary.getDefaultAmountFactory()
                .setCurrency(currencyUnit)
                .setNumber(number)
                .create();
    }
}

package au.com.sportsbet.movietickets.config;

import org.javamoney.moneta.Money;
import org.springframework.boot.jackson.JacksonComponent;
import org.springframework.boot.jackson.ObjectValueDeserializer;
import org.springframework.boot.jackson.ObjectValueSerializer;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.SerializationContext;

import javax.money.MonetaryAmount;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class JacksonConfig {
    @JacksonComponent(type = MonetaryAmount.class)
    static class MonetaryAmountSerializer extends ObjectValueSerializer<MonetaryAmount> {
        @Override
        protected void serializeObject(MonetaryAmount value, JsonGenerator gen, SerializationContext ctxt) {
            int scale = value.getCurrency().getDefaultFractionDigits();
            BigDecimal amount = value.getNumber().numberValueExact(BigDecimal.class).setScale(scale, RoundingMode.HALF_EVEN);
            gen.writeStringProperty("amount", amount.toPlainString());
            gen.writeStringProperty("currency", value.getCurrency().getCurrencyCode());
        }
    }

    @JacksonComponent(type = MonetaryAmount.class)
    static class MonetaryAmountDeserializer extends ObjectValueDeserializer<MonetaryAmount> {
        @Override
        protected MonetaryAmount deserializeObject(tools.jackson.core.JsonParser p, tools.jackson.databind.DeserializationContext ctxt, JsonNode node) {
            String amount = getRequiredNode(node, "amount").asString();
            String currency = getRequiredNode(node, "currency").asString();
            return Money.of(new BigDecimal(amount), currency);
        }
    }
}

package au.com.sportsbet.movietickets.config;

import com.fasterxml.jackson.databind.JavaType;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.core.converter.ModelConverterContext;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.money.MonetaryAmount;
import java.util.Iterator;

@Component
@Order(Integer.MIN_VALUE)
public class MonetaryAmountModelConverter implements ModelConverter {

    @Override
    public Schema resolve(AnnotatedType type, ModelConverterContext context, Iterator<ModelConverter> chain) {
        JavaType javaType = Json.mapper().constructType(type.getType());
        if (javaType != null && MonetaryAmount.class.isAssignableFrom(javaType.getRawClass())) {
            return new ObjectSchema()
                    .addProperty("amount", new StringSchema().example("25.00"))
                    .addProperty("currency", new StringSchema().example("AUD"));
        }
        return chain.hasNext() ? chain.next().resolve(type, context, chain) : null;
    }
}

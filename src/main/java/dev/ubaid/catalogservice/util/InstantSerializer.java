package dev.ubaid.catalogservice.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class InstantSerializer extends JsonSerializer<Instant> {

    @Override
    public void serialize(Instant value, JsonGenerator gen, SerializerProvider serializers)
        throws IOException {
        value = value.truncatedTo(ChronoUnit.MICROS);
        BigDecimal millis = BigDecimal.valueOf(value.toEpochMilli());
        gen.writeNumber(millis);
    }
}

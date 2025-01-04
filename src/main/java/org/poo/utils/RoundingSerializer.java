package org.poo.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class RoundingSerializer extends JsonSerializer {
    private static final int DECIMALS = 5;

    /**
     * Serialize a double value to round it to three decimals
     * @param o - value
     * @param gen - generator
     * @param serializers - serializers
     */
    @Override
    public void serialize(final Object o, final JsonGenerator gen,
                          final SerializerProvider serializers) throws IOException {
        Double value = (Double) o;
        if (value == null) {
            gen.writeNull();
        } else {
            double rounded = new BigDecimal(value)
                    .setScale(DECIMALS, RoundingMode.HALF_DOWN).doubleValue();
            gen.writeNumber(rounded);
        }
    }
}

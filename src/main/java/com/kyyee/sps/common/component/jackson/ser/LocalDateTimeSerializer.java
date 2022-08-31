package com.kyyee.sps.common.component.jackson.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class LocalDateTimeSerializer extends StdSerializer<LocalDateTime> {
    public static final LocalDateTimeSerializer INSTANCE = new LocalDateTimeSerializer();

    private static final long serialVersionUID = 3438194048373180911L;

    private LocalDateTimeSerializer() {
        super(LocalDateTime.class);
    }

    @Override
    public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (!ObjectUtils.isEmpty(value)) {
            final long mills = value.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            gen.writeNumber(mills);
        } else {
            gen.writeNull();
        }
    }
}

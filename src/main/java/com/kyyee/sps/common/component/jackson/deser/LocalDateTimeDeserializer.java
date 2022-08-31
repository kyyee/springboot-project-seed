package com.kyyee.sps.common.component.jackson.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class LocalDateTimeDeserializer extends StdDeserializer<LocalDateTime> {
    public static final LocalDateTimeDeserializer INSTANCE = new LocalDateTimeDeserializer();

    private static final long serialVersionUID = 4383116454477150296L;

    private LocalDateTimeDeserializer() {
        super(LocalDateTime.class);
    }

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (!ObjectUtils.isEmpty(p)) {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(p.getText())), ZoneId.systemDefault());
        } else {
            return null;
        }
    }
}

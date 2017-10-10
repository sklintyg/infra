package se.inera.intyg.infra.loggtjanststub.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class LogStoreObjectMapper extends ObjectMapper {

    public LogStoreObjectMapper() {
        setSerializationInclusion(JsonInclude.Include.ALWAYS);
        configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        registerModule(new Module());

        setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
    }

    private static final class Module extends SimpleModule {
        private Module() {
            addSerializer(LocalDateTime.class, LocalDateTimeSerializer.INSTANCE);
            addDeserializer(LocalDateTime.class, LocalDateTimeDeserializer.INSTANCE);

            addSerializer(LocalDate.class, LocalDateSerializer.INSTANCE);
            addDeserializer(LocalDate.class, LocalDateDeserializer.INSTANCE);
        }
    }
}

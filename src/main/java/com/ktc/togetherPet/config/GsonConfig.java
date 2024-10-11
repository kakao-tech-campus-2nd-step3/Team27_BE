package com.ktc.togetherPet.config;

import static com.google.gson.FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.ktc.togetherPet.exception.ErrorMessage;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GsonConfig {

    @Bean
    public Gson gson() {
        return new GsonBuilder()
            .setFieldNamingPolicy(LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer())
            .registerTypeAdapter(ErrorMessage.class, new ErrorMessageSerializer())
            .setPrettyPrinting()
            .create();
    }

    private static class LocalDateTimeSerializer implements JsonSerializer<LocalDateTime> {

        private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
            "yyyy-MM-dd HH:mm:ss");

        @Override
        public JsonElement serialize(
            LocalDateTime localDateTime,
            Type typeOfSrc,
            JsonSerializationContext context
        ) {
            return new JsonPrimitive(formatter.format(localDateTime));
        }
    }

    private static class LocalDateTimeDeserializer implements JsonDeserializer<LocalDateTime> {

        @Override
        public LocalDateTime deserialize(
            JsonElement json,
            Type typeOfT,
            JsonDeserializationContext context
        ) throws JsonParseException {
            return LocalDateTime.parse(
                json.getAsString(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            );
        }
    }

    private static class ErrorMessageSerializer implements JsonSerializer<ErrorMessage> {

        @Override
        public JsonElement serialize(ErrorMessage src, Type typeOfSrc,
            JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("code", src.getCode());
            jsonObject.addProperty("message", src.getMessage());
            return jsonObject;
        }
    }
}

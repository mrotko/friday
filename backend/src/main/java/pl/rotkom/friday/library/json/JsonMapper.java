package pl.rotkom.friday.library.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import reactor.core.publisher.Mono;
import reactor.netty.ByteBufMono;

public class JsonMapper {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .registerModule(new JavaTimeModule());

    public static String serialize(Object object) {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T deserialize(String response, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(response, clazz);
        } catch (Exception e) {
            throw new RuntimeException(response, e);
        }
    }

    public static <T> T convert(Object object, Class<T> clazz) {
        return OBJECT_MAPPER.convertValue(object, clazz);
    }

    public static <T> T convert(Object object, TypeReference<T> clazz) {
        return OBJECT_MAPPER.convertValue(object, clazz);
    }

    public static <T> Mono<T> deserialize(ByteBufMono buf, Class<T> clazz) {
        return buf.asString().flatMap(body -> Mono.just(deserialize(body, clazz)));
    }

    public static <T> Mono<T> deserialize(ByteBufMono buf, TypeReference<T> clazz) {
        return buf.asString().flatMap(body -> Mono.just(deserialize(body, clazz)));
    }

    public static <T> T deserialize(String response, TypeReference<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(response, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

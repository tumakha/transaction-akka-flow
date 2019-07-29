package transaction.flow.processor;

import akka.NotUsed;
import akka.stream.javadsl.Flow;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;

/**
 * @author Yuriy Tumakha
 */
interface JsonSupport {

  ObjectMapper objectMapper = new ObjectMapper()
      .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
      .disable(DeserializationFeature.ACCEPT_FLOAT_AS_INT)
      .disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
      .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
      .enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING)
      .enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
      .setSerializationInclusion(Include.NON_NULL)
      .registerModule(new Jdk8Module())
      .registerModule(new JavaTimeModule());

  default <T> Flow<String, T, NotUsed> parseJson(Class<T> valueClass) {
    return Flow.<String, T>fromFunction(json -> fromJson(json, valueClass)).async();
  }

  default <T> Flow<T, String, NotUsed> convertToJson() {
    return Flow.<T, String>fromFunction(this::toJson).async();
  }

  default <T> T fromJson(String json, Class<T> valueClass) throws IOException {
    return objectMapper.readValue(json, valueClass);
  }

  default <T> T fromJson(String json, TypeReference valueTypeRef) throws IOException {
    return objectMapper.readValue(json, valueTypeRef);
  }

  default String toJson(Object value) throws IOException {
    return objectMapper.writeValueAsString(value);
  }

}

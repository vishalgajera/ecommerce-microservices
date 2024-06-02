package com.order.utility;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@Component
public class GlobalUtility {

    public static String convertClassToJson(final Object input) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Include.NON_NULL);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper.writeValueAsString(input);
    }

    public static final List<HashMap<String, String>> convertStringJsonToMap(String jsonInput) throws IOException {
        TypeReference<List<HashMap<String, String>>> typeRef = new TypeReference<List<HashMap<String, String>>>() {
        };
        ObjectMapper mapper = new ObjectMapper();
        List<HashMap<String, String>> map = mapper.readValue(jsonInput, typeRef);
        return map;
    }

}

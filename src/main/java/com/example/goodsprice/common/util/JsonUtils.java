package com.example.goodsprice.common.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class JsonUtils {

  private JsonUtils() {}

  private static final ObjectMapper MAPPER = new ObjectMapper();

  @SuppressWarnings("unchecked")
  public static List<Map<String, Object>> extractItems(String extractedDataJson) {
    if (Objects.isNull(extractedDataJson) || extractedDataJson.isBlank())
      return Collections.emptyList();
    try {
      var data = MAPPER.readValue(extractedDataJson, new TypeReference<Map<String, Object>>() {});
      var itemsRaw = data.get("items");
      if (Objects.isNull(itemsRaw) || !(itemsRaw instanceof List)) return Collections.emptyList();
      return (List<Map<String, Object>>) itemsRaw;
    } catch (Exception e) {
      return Collections.emptyList();
    }
  }

  public static String toJson(Object value) {
    if (Objects.isNull(value)) return "[]";
    try {
      return MAPPER.writeValueAsString(value);
    } catch (Exception e) {
      return "[]";
    }
  }

  public static String hash256(Object value) {
    var json = toJson(value);
    return HashUtils.sha256(json.getBytes(StandardCharsets.UTF_8));
  }

  public static Map<String, Object> parseJson(String json) {
    if (Objects.isNull(json) || json.isBlank()) return Collections.emptyMap();
    try {
      return MAPPER.readValue(json, new TypeReference<Map<String, Object>>() {});
    } catch (Exception e) {
      return Collections.emptyMap();
    }
  }
}

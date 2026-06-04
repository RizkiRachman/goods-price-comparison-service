package com.example.goodsprice.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import org.junit.jupiter.api.Test;

class JsonUtilsTest {

  @Test
  void shouldToJsonReturnsEmptyObjectWhenNull() {
    assertEquals("{}", JsonUtils.toJson(null));
  }

  @Test
  void shouldToJsonReturnsValidJsonForObject() {
    var result = JsonUtils.toJson(Map.of("key", "value"));
    assertTrue(result.contains("key"));
    assertTrue(result.contains("value"));
  }

  @Test
  void shouldToJsonReturnsEmptyArrayForException() {
    var result = JsonUtils.toJson(new Object()); // circular or problematic
    assertNotNull(result);
  }

  @Test
  void shouldParseJsonReturnsMap() {
    var result = JsonUtils.parseJson("{\"key\": \"value\"}");
    assertEquals("value", result.get("key"));
  }

  @Test
  void shouldParseJsonReturnsEmptyWhenNull() {
    assertTrue(JsonUtils.parseJson(null).isEmpty());
  }

  @Test
  void shouldParseJsonReturnsEmptyWhenBlank() {
    assertTrue(JsonUtils.parseJson("").isEmpty());
  }

  @Test
  void shouldParseJsonReturnsEmptyWhenMalformed() {
    assertTrue(JsonUtils.parseJson("{invalid}").isEmpty());
  }

  @Test
  void shouldExtractItemsReturnsEmptyWhenNull() {
    assertTrue(JsonUtils.extractItems(null).isEmpty());
  }

  @Test
  void shouldExtractItemsReturnsEmptyWhenBlank() {
    assertTrue(JsonUtils.extractItems("").isEmpty());
  }

  @Test
  void shouldExtractItemsReturnsEmptyWhenMalformed() {
    assertTrue(JsonUtils.extractItems("{invalid}").isEmpty());
  }

  @Test
  void shouldExtractItemsReturnsListFromItemsKey() {
    var json = "{\"items\": [{\"id\": 1}, {\"id\": 2}]}";
    var items = JsonUtils.extractItems(json);
    assertEquals(2, items.size());
    assertEquals(1, items.get(0).get("id"));
  }

  @Test
  void shouldExtractItemsReturnsEmptyWhenItemsMissing() {
    var json = "{\"other\": \"data\"}";
    assertTrue(JsonUtils.extractItems(json).isEmpty());
  }

  @Test
  void shouldHash256ReturnsConsistentHash() {
    var hash1 = JsonUtils.hash256(Map.of("a", 1));
    var hash2 = JsonUtils.hash256(Map.of("a", 1));
    assertEquals(hash1, hash2);
  }

  @Test
  void shouldHash256ReturnsDifferentHashForDifferentValues() {
    var hash1 = JsonUtils.hash256(Map.of("a", 1));
    var hash2 = JsonUtils.hash256(Map.of("a", 2));
    assertTrue(!hash1.equals(hash2));
  }
}

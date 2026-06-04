package com.example.goodsprice.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import java.io.IOException;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LenientOffsetDateTimeDeserializerTest {

  @Mock private JsonParser parser;
  @Mock private DeserializationContext context;

  private LenientOffsetDateTimeDeserializer deserializer;

  @BeforeEach
  void setUp() {
    deserializer = new LenientOffsetDateTimeDeserializer();
  }

  @Test
  void shouldDeserializeFullDateTime() throws Exception {
    when(parser.getText()).thenReturn("2024-01-15T10:30:00+07:00");

    var result = deserializer.deserialize(parser, context);

    assertNotNull(result);
    assertEquals(2024, result.getYear());
    assertEquals(1, result.getMonthValue());
    assertEquals(15, result.getDayOfMonth());
  }

  @Test
  void shouldDeserializeDateOnly() throws Exception {
    when(parser.getText()).thenReturn("2024-01-15");

    var result = deserializer.deserialize(parser, context);

    assertNotNull(result);
    assertEquals(2024, result.getYear());
    assertEquals(1, result.getMonthValue());
    assertEquals(15, result.getDayOfMonth());
    assertEquals(ZoneOffset.UTC, result.getOffset());
  }

  @Test
  void shouldReturnNullWhenTextIsNull() throws Exception {
    when(parser.getText()).thenReturn(null);

    var result = deserializer.deserialize(parser, context);

    assertEquals(null, result);
  }

  @Test
  void shouldReturnNullWhenTextIsBlank() throws Exception {
    when(parser.getText()).thenReturn("  ");

    var result = deserializer.deserialize(parser, context);

    assertEquals(null, result);
  }

  @Test
  void shouldThrowWhenTextIsInvalid() throws Exception {
    when(parser.getText()).thenReturn("not-a-date");

    assertThrows(IOException.class, () -> deserializer.deserialize(parser, context));
  }
}

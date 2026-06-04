package com.example.goodsprice.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class PipelineTest {

  @Test
  void shouldCreatePipelineWithValue() {
    var pipe = Pipeline.of(5);
    assertNotNull(pipe);
    assertEquals(5, pipe.value());
  }

  @Test
  void shouldApplyTransformation() {
    var result = Pipeline.of(5).then(x -> x * 2).then(x -> x + 1);
    assertEquals(11, result.value());
  }

  @Test
  void shouldHandleStringTransformation() {
    var result = Pipeline.of("hello").then(String::toUpperCase);
    assertEquals("HELLO", result.value());
  }

  @Test
  void shouldHandleMultipleTypeChanges() {
    var result = Pipeline.of("42").then(Integer::parseInt).then(x -> x * 2);
    assertEquals(84, result.value());
  }
}

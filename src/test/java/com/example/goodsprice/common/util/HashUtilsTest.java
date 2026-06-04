package com.example.goodsprice.common.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

@SuppressWarnings("checkstyle:MethodName")
class HashUtilsTest {

  @Test
  void shouldHashBytes() {
    var result = HashUtils.sha256("hello".getBytes());
    assertThat(result).isNotNull();
    assertThat(result).isInstanceOf(String.class);
  }

  @Test
  void shouldProduceConsistentHash() {
    var result1 = HashUtils.sha256("hello".getBytes());
    var result2 = HashUtils.sha256("hello".getBytes());
    assertThat(result1).isEqualTo(result2);
  }
}

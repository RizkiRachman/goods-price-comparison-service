package com.example.goodsprice.common.util;

import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;

public final class EnumParser {

  private EnumParser() {}

  public static <E extends Enum<E>> E parse(
      String value, Class<E> enumType, String fieldName, Consumer<String> warnLogger) {
    if (Objects.isNull(value) || value.isBlank()) {
      return null;
    }
    try {
      return Enum.valueOf(enumType, value.toUpperCase(Locale.ROOT));
    } catch (IllegalArgumentException e) {
      warnLogger.accept("Invalid " + fieldName + " filter: " + value);
      return null;
    }
  }
}

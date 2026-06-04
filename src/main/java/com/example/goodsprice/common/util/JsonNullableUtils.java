package com.example.goodsprice.common.util;

import jakarta.annotation.Nullable;
import java.util.Objects;
import org.openapitools.jackson.nullable.JsonNullable;

public final class JsonNullableUtils {

  private JsonNullableUtils() {}

  @Nullable
  public static <T> T resolveNullable(JsonNullable<T> nullable) {
    if (Objects.isNull(nullable)) return null;
    return nullable.orElse(null);
  }
}

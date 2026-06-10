package com.example.goodsprice.common.util;

import java.util.Objects;

public final class LogSanitizer {

  private LogSanitizer() {}

  public static String sanitize(Object input) {
    if (Objects.isNull(input)) {
      return null;
    }
    return input
        .toString()
        .replace('\r', ' ')
        .replace('\n', ' ')
        .replaceAll("[\\p{Cntrl}&&[^\t]]", "");
  }

  public static String sanitize(Object input, int maxLength) {
    var result = sanitize(input);
    if (Objects.nonNull(result) && result.length() > maxLength) {
      return result.substring(0, maxLength);
    }
    return result;
  }
}

package com.example.goodsprice.common.util;

import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

public final class ProductNameUtils {

  private static final Pattern UNIT_SUFFIX_PATTERN =
      Pattern.compile(
          "(?:"
              + "/\\s*KG\\s*"
              + "|/\\s*GR(?:A|AM)?\\s*"
              + "|\\s+\\d+(?:[.,]\\d+)?\\s*(?:KG|GR|G)\\s*"
              + ")\\s*$",
          Pattern.CASE_INSENSITIVE);

  private static final Set<String> WEIGHT_UNITS = Set.of("KILOGRAM", "GRAM");

  private ProductNameUtils() {}

  public static String cleanProductName(String productName, String unitType) {
    if (Objects.isNull(productName) || productName.isBlank()) return productName;
    if (Objects.isNull(unitType)) return productName;
    if (!WEIGHT_UNITS.contains(unitType.toUpperCase(Locale.ROOT))) return productName;

    var matcher = UNIT_SUFFIX_PATTERN.matcher(productName);
    if (matcher.find()) {
      return productName.substring(0, matcher.start()).trim();
    }
    return productName.trim();
  }
}

package com.example.goodsprice.common.constant;

import java.util.Locale;
import java.util.Objects;
import java.util.Set;

public final class UnitConstants {

  private UnitConstants() {}

  public static final Set<String> WEIGHT_UNITS = Set.of("KG", "KILOGRAM", "GRAM", "G", "ONS");

  public static boolean isWeight(String unit) {
    if (Objects.isNull(unit)) {
      return false;
    }
    return WEIGHT_UNITS.contains(unit.toUpperCase(Locale.ROOT));
  }
}

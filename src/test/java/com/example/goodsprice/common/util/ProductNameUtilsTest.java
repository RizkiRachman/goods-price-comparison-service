package com.example.goodsprice.common.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProductNameUtilsTest {

  @Test
  @DisplayName("Should strip weight suffix for KILOGRAM items")
  void shouldStripWeightSuffixForKilogram() {
    var result = ProductNameUtils.cleanProductName("QUEEN FOOD SHRIMP ROLL 350GR", "KILOGRAM");
    assertEquals("QUEEN FOOD SHRIMP ROLL", result);
  }

  @Test
  @DisplayName("Should strip weight suffix for GRAM items")
  void shouldStripWeightSuffixForGram() {
    var result = ProductNameUtils.cleanProductName("FORVITA MARGARINE 250GR", "GRAM");
    assertEquals("FORVITA MARGARINE", result);
  }

  @Test
  @DisplayName("Should not modify PIECE items even with weight suffix in name")
  void shouldNotModifyPieceItems() {
    var result =
        ProductNameUtils.cleanProductName("SO GOOD CHICKEN NUGGET MAC&CHEESE 400GR", "PIECE");
    assertEquals("SO GOOD CHICKEN NUGGET MAC&CHEESE 400GR", result);
  }

  @Test
  @DisplayName("Should not modify LITER items (excluded)")
  void shouldNotModifyLiterItems() {
    var result = ProductNameUtils.cleanProductName("E+ WATER BTL 250ML", "LITER");
    assertEquals("E+ WATER BTL 250ML", result);
  }

  @Test
  @DisplayName("Should keep name as-is when no pattern matches")
  void shouldKeepNameWhenNoPatternMatches() {
    var result = ProductNameUtils.cleanProductName("BAWANG PUTIH KATING", "KILOGRAM");
    assertEquals("BAWANG PUTIH KATING", result);
  }

  @Test
  @DisplayName("Should keep name as-is for GRAM with no suffix")
  void shouldKeepNameForGramWithNoSuffix() {
    var result = ProductNameUtils.cleanProductName("SPRD PISANG CAVENDISH", "KILOGRAM");
    assertEquals("SPRD PISANG CAVENDISH", result);
  }

  @Test
  @DisplayName("Should strip /KG suffix")
  void shouldStripKiloSlashSuffix() {
    var result = ProductNameUtils.cleanProductName("BRASTAGI WORTEL CURAH /KG", "KILOGRAM");
    assertEquals("BRASTAGI WORTEL CURAH", result);
  }

  @Test
  @DisplayName("Should strip /GR suffix")
  void shouldStripGramSlashSuffix() {
    var result = ProductNameUtils.cleanProductName("PRODUK CONTOH /GR", "GRAM");
    assertEquals("PRODUK CONTOH", result);
  }

  @Test
  @DisplayName("Should strip /GRA suffix")
  void shouldStripGramSlashGraSuffix() {
    var result = ProductNameUtils.cleanProductName("PRODUK CONTOH /GRA", "GRAM");
    assertEquals("PRODUK CONTOH", result);
  }

  @Test
  @DisplayName("Should strip /GRAM suffix")
  void shouldStripGramSlashGramSuffix() {
    var result = ProductNameUtils.cleanProductName("PRODUK CONTOH /GRAM", "GRAM");
    assertEquals("PRODUK CONTOH", result);
  }

  @Test
  @DisplayName("Should strip / KG with space")
  void shouldStripKiloSlashWithSpace() {
    var result = ProductNameUtils.cleanProductName("BRASTAGI WORTEL CURAH/ KG", "KILOGRAM");
    assertEquals("BRASTAGI WORTEL CURAH", result);
  }

  @Test
  @DisplayName("Should strip decimal weight suffix")
  void shouldStripDecimalWeightSuffix() {
    var result = ProductNameUtils.cleanProductName("PRODUK 1.5KG", "KILOGRAM");
    assertEquals("PRODUK", result);
  }

  @Test
  @DisplayName("Should strip weight suffix with comma decimal")
  void shouldStripWeightSuffixWithCommaDecimal() {
    var result = ProductNameUtils.cleanProductName("PRODUK 0,25 KG", "KILOGRAM");
    assertEquals("PRODUK", result);
  }

  @Test
  @DisplayName("Should strip G suffix")
  void shouldStripGSuffix() {
    var result = ProductNameUtils.cleanProductName("PRODUK 500 G", "GRAM");
    assertEquals("PRODUK", result);
  }

  @Test
  @DisplayName("Should handle null productName")
  void shouldHandleNullProductName() {
    assertNull(ProductNameUtils.cleanProductName(null, "KILOGRAM"));
  }

  @Test
  @DisplayName("Should handle blank productName")
  void shouldHandleBlankProductName() {
    assertEquals("  ", ProductNameUtils.cleanProductName("  ", "KILOGRAM"));
  }

  @Test
  @DisplayName("Should handle null unitType")
  void shouldHandleNullUnitType() {
    var result = ProductNameUtils.cleanProductName("QUEEN FOOD SHRIMP ROLL 350GR", null);
    assertEquals("QUEEN FOOD SHRIMP ROLL 350GR", result);
  }

  @Test
  @DisplayName("Should handle case-insensitive unitType")
  void shouldHandleCaseInsensitiveUnitType() {
    var result = ProductNameUtils.cleanProductName("QUEEN FOOD SHRIMP ROLL 350GR", "kilogram");
    assertEquals("QUEEN FOOD SHRIMP ROLL", result);
  }

  @Test
  @DisplayName("Should handle case-insensitive suffix")
  void shouldHandleCaseInsensitiveSuffix() {
    var result = ProductNameUtils.cleanProductName("QUEEN FOOD SHRIMP ROLL 350gr", "GRAM");
    assertEquals("QUEEN FOOD SHRIMP ROLL", result);
  }

  @Test
  @DisplayName("Should strip trailing whitespace in suffix")
  void shouldStripTrailingWhitespaceInSuffix() {
    var result = ProductNameUtils.cleanProductName("QUEEN FOOD SHRIMP ROLL 350GR  ", "KILOGRAM");
    assertEquals("QUEEN FOOD SHRIMP ROLL", result);
  }

  @Test
  @DisplayName("Should handle millilitre suffix for KILOGRAM by stripping it")
  void shouldHandleMlSuffixForKilogram() {
    var result = ProductNameUtils.cleanProductName("PRODUK 500ML", "KILOGRAM");
    assertEquals("PRODUK 500ML", result);
  }

  @Test
  @DisplayName("Should strip weight suffix with multiple spaces before number")
  void shouldStripWeightSuffixWithMultipleSpaces() {
    var result = ProductNameUtils.cleanProductName("QUEEN FOOD SHRIMP ROLL   350GR", "KILOGRAM");
    assertEquals("QUEEN FOOD SHRIMP ROLL", result);
  }

  @Test
  @DisplayName("Should handle unknown unit type by returning original name")
  void shouldHandleUnknownUnitType() {
    var result = ProductNameUtils.cleanProductName("QUEEN FOOD SHRIMP ROLL 350GR", "UNKNOWN");
    assertEquals("QUEEN FOOD SHRIMP ROLL 350GR", result);
  }
}

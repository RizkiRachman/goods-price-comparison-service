package com.example.goodsprice.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import org.junit.jupiter.api.Test;

class DateUtilsTest {

  @Test
  void shouldFormatLocalDate() {
    assertEquals("2026-06-03", DateUtils.format(LocalDate.of(2026, 6, 3), "yyyy-MM-dd"));
  }

  @Test
  void shouldReturnNullWhenLocalDateIsNull() {
    assertNull(DateUtils.format((LocalDate) null, "yyyy-MM-dd"));
  }

  @Test
  void shouldReturnNullWhenPatternIsNullForLocalDate() {
    assertNull(DateUtils.format(LocalDate.now(), null));
  }

  @Test
  void shouldFormatLocalDateTime() {
    assertEquals("2026-06-03 10:30",
        DateUtils.format(LocalDateTime.of(2026, 6, 3, 10, 30), "yyyy-MM-dd HH:mm"));
  }

  @Test
  void shouldReturnNullWhenLocalDateTimeIsNull() {
    assertNull(DateUtils.format((LocalDateTime) null, "yyyy-MM-dd"));
  }

  @Test
  void shouldFormatOffsetDateTime() {
    assertEquals("2026-06-03T10:30",
        DateUtils.format(OffsetDateTime.of(2026, 6, 3, 10, 30, 0, 0, ZoneOffset.UTC),
            "yyyy-MM-dd'T'HH:mm"));
  }

  @Test
  void shouldReturnNullWhenOffsetDateTimeIsNull() {
    assertNull(DateUtils.format((OffsetDateTime) null, "yyyy-MM-dd"));
  }

  @Test
  void shouldFormatDate() {
    var date = Date.from(LocalDate.of(2026, 6, 3).atStartOfDay(ZoneOffset.UTC).toInstant());
    assertEquals("2026-06-03", DateUtils.format(date, "yyyy-MM-dd"));
  }

  @Test
  void shouldReturnNullWhenDateIsNull() {
    assertNull(DateUtils.format((Date) null, "yyyy-MM-dd"));
  }
}

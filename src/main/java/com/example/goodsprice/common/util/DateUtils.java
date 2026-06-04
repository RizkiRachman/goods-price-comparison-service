package com.example.goodsprice.common.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;

public final class DateUtils {

  private DateUtils() {}

  public static final String ISO_DATE = "yyyy-MM-dd";
  public static final String ISO_DATE_TIME = "yyyy-MM-dd HH:mm:ss";
  public static final String ISO_DATE_TIME_TZ = "yyyy-MM-dd'T'HH:mm:ssXXX";
  public static final String SHORT_DATE = "dd/MM/yyyy";
  public static final String LONG_DATE = "dd MMMM yyyy";
  public static final String TIMESTAMP = "yyyy-MM-dd HH:mm:ss.SSS";

  public static String format(LocalDate date, String pattern) {
    if (Objects.isNull(date) || Objects.isNull(pattern)) return null;
    return DateTimeFormatter.ofPattern(pattern).format(date);
  }

  public static String format(LocalDateTime dateTime, String pattern) {
    if (Objects.isNull(dateTime) || Objects.isNull(pattern)) return null;
    return DateTimeFormatter.ofPattern(pattern).format(dateTime);
  }

  public static String format(OffsetDateTime dateTime, String pattern) {
    if (Objects.isNull(dateTime) || Objects.isNull(pattern)) return null;
    return DateTimeFormatter.ofPattern(pattern).format(dateTime);
  }

  @SuppressWarnings("PMD.ReplaceJavaUtilDate")
  public static String format(Date date, String pattern) {
    if (Objects.isNull(date) || Objects.isNull(pattern)) return null;
    return format(date.toInstant().atOffset(ZoneOffset.UTC), pattern);
  }
}

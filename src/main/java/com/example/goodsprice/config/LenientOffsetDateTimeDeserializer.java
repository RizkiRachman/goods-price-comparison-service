package com.example.goodsprice.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LenientOffsetDateTimeDeserializer extends JsonDeserializer<OffsetDateTime> {

  @Override
  public OffsetDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    var text = p.getText();
    if (Objects.isNull(text) || text.isBlank()) return null;

    try {
      return OffsetDateTime.parse(text, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    } catch (DateTimeParseException e) {
      log.debug("ISO_OFFSET_DATE_TIME parse failed for '{}', trying ISO_LOCAL_DATE fallback", text);
    }

    try {
      var date = LocalDate.parse(text, DateTimeFormatter.ISO_LOCAL_DATE);
      return date.atStartOfDay(ZoneOffset.UTC).toOffsetDateTime();
    } catch (DateTimeParseException e) {
      throw new IOException(
          "Cannot parse date '%s'. Expected format: yyyy-MM-dd or yyyy-MM-ddTHH:mm:ssXXX"
              .formatted(text),
          e);
    }
  }
}

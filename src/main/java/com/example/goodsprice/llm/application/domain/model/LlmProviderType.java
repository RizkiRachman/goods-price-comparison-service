package com.example.goodsprice.llm.application.domain.model;

import java.util.Locale;
import java.util.Objects;

public enum LlmProviderType {
  LOCAL,
  OPENAI,
  ANTHROPIC,
  GEMINI,
  GROQ,
  SUMOPOD;

  public static LlmProviderType fromValue(String value) {
    if (Objects.isNull(value) || value.isBlank()) return LOCAL;
    return switch (value.toLowerCase(Locale.ROOT)) {
      case "openai" -> OPENAI;
      case "anthropic" -> ANTHROPIC;
      case "gemini" -> GEMINI;
      case "groq" -> GROQ;
      case "sumopod" -> SUMOPOD;
      default -> LOCAL;
    };
  }
}

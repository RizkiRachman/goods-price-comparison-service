package com.example.goodsprice.common.util;

import java.util.function.Function;

public record Pipeline<T>(T value) {

  public static <T> Pipeline<T> of(T value) {
    return new Pipeline<>(value);
  }

  public <R> Pipeline<R> then(Function<T, R> step) {
    return Pipeline.of(step.apply(value));
  }
}

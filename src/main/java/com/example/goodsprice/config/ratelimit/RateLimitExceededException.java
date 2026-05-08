package com.example.goodsprice.config.ratelimit;

public class RateLimitExceededException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private final long limit;
  private final long retryAfterSeconds;

  public RateLimitExceededException(long limit, long retryAfterSeconds) {
    super("Too many requests. Please try again after " + retryAfterSeconds + " seconds.");
    this.limit = limit;
    this.retryAfterSeconds = retryAfterSeconds;
  }

  public long getLimit() {
    return limit;
  }

  public long getRetryAfterSeconds() {
    return retryAfterSeconds;
  }
}

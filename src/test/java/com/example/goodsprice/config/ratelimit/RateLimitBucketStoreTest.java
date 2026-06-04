package com.example.goodsprice.config.ratelimit;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

class RateLimitBucketStoreTest {

  private final RateLimitBucketStore store = new RateLimitBucketStore();

  @Test
  void shouldCreateNewBucketForNewKey() {
    var bucket = store.getOrCreate("client::/api/test", 10, 60);

    assertNotNull(bucket);
    var probe = bucket.tryConsumeAndReturnRemaining(1);
    assertNotNull(probe);
  }

  @Test
  void shouldReturnSameBucketForSameKey() {
    var first = store.getOrCreate("same-key", 10, 60);
    var second = store.getOrCreate("same-key", 10, 60);

    assertSame(first, second);
  }

  @Test
  void shouldCreateDifferentBucketsForDifferentKeys() {
    var first = store.getOrCreate("key-1", 10, 60);
    var second = store.getOrCreate("key-2", 5, 30);

    assertNotNull(first);
    assertNotNull(second);
  }

  @Test
  void shouldConsumeTokens() {
    var bucket = store.getOrCreate("consume-test", 5, 60);

    for (int i = 0; i < 5; i++) {
      assertNotNull(bucket.tryConsumeAndReturnRemaining(1));
    }
  }
}

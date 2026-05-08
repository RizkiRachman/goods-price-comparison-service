package com.example.goodsprice.config.ratelimit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Component;

@Component
public class RateLimitBucketStore {

  private final Cache<String, Bucket> buckets =
      Caffeine.newBuilder().expireAfterAccess(2, TimeUnit.HOURS).maximumSize(100_000).build();

  public Bucket getOrCreate(String key, long limit, long windowSeconds) {
    return buckets.get(
        key,
        k ->
            Bucket.builder()
                .addLimit(
                    Bandwidth.builder()
                        .capacity(limit)
                        .refillGreedy(limit, Duration.ofSeconds(windowSeconds))
                        .build())
                .build());
  }
}

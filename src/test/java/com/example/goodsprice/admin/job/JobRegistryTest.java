package com.example.goodsprice.admin.job;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JobRegistryTest {

  private JobRegistry registry;

  @BeforeEach
  void setUp() {
    registry = new JobRegistry();
  }

  @Test
  @DisplayName("Should register and retrieve executor")
  void shouldRegisterAndRetrieve() {
    JobExecutor executor = () -> {};
    registry.register("dataSync", executor);

    var result = registry.get("dataSync");

    assertNotNull(result);
    assertTrue(registry.contains("dataSync"));
  }

  @Test
  @DisplayName("Should return null for unregistered job")
  void shouldReturnNullForUnknownJob() {
    assertNull(registry.get("unknownJob"));
    assertFalse(registry.contains("unknownJob"));
  }

  @Test
  @DisplayName("Should overwrite existing executor on re-register")
  void shouldOverwriteOnReRegister() {
    JobExecutor executor1 = () -> {};
    JobExecutor executor2 = () -> {};
    registry.register("dataSync", executor1);
    registry.register("dataSync", executor2);

    assertTrue(registry.contains("dataSync"));
    assertNotNull(registry.get("dataSync"));
  }

  @Test
  @DisplayName("Should handle multiple job registrations")
  void shouldHandleMultipleJobs() {
    registry.register("job1", () -> {});
    registry.register("job2", () -> {});

    assertTrue(registry.contains("job1"));
    assertTrue(registry.contains("job2"));
    assertNotNull(registry.get("job1"));
    assertNotNull(registry.get("job2"));
  }
}

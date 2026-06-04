package com.example.goodsprice.admin.job;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JobExecutorTest {

  @Test
  @DisplayName("Should execute runnable without exception")
  void shouldExecute() {
    JobExecutor executor = () -> {};
    assertDoesNotThrow(executor::run);
  }

  @Test
  @DisplayName("Should execute custom logic")
  void shouldExecuteCustomLogic() {
    var result = new boolean[] {false};
    JobExecutor executor = () -> result[0] = true;
    executor.run();
    assertDoesNotThrow(executor::run);
  }
}

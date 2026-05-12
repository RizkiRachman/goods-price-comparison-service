package com.example.goodsprice.common.test.extension;

import com.example.goodsprice.common.test.annotation.RetryTest;
import java.lang.reflect.Method;
import java.util.logging.Logger;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;

public class TestRetryExtension implements InvocationInterceptor {

  private static final Logger LOG = Logger.getLogger(TestRetryExtension.class.getName());

  @Override
  public void interceptTestMethod(
      Invocation<Void> invocation,
      ReflectiveInvocationContext<Method> invocationContext,
      ExtensionContext extensionContext)
      throws Throwable {
    var retry =
        extensionContext.getTestMethod().map(m -> m.getAnnotation(RetryTest.class)).orElse(null);
    int maxRetries = retry != null ? retry.value() : 1;
    var testName = extensionContext.getDisplayName();

    if (maxRetries <= 0) {
      invocation.proceed();
      return;
    }

    for (int attempt = 1; attempt <= maxRetries; attempt++) {
      try {
        invocation.proceed();
        return;
      } catch (Throwable t) {
        if (attempt < maxRetries) {
          LOG.warning(
              "Test '%s' failed on attempt %d/%d: %s. Retrying..."
                  .formatted(testName, attempt, maxRetries, t.getMessage()));
        } else {
          throw t;
        }
      }
    }
  }
}

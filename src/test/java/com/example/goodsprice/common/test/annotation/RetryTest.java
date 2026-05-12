package com.example.goodsprice.common.test.annotation;

import com.example.goodsprice.common.test.extension.TestRetryExtension;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(TestRetryExtension.class)
public @interface RetryTest {

  int value() default 3;
}

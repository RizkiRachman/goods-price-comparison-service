package com.example.goodsprice.activity.infrastructure.aspect;

import com.example.goodsprice.activity.application.annotation.ActivityLog;
import com.example.goodsprice.activity.application.domain.model.ActivityLogDomain;
import com.example.goodsprice.activity.application.port.out.ActivityLogEventOutPort;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.Advisor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ActivityLogAspect {

  @Bean
  @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
  public static Advisor activityLogAdvisor(ActivityLogEventOutPort eventOutPort) {
    var pointcut = AnnotationMatchingPointcut.forMethodAnnotation(ActivityLog.class);
    var interceptor = (MethodInterceptor) new ActivityLogInterceptor(eventOutPort);
    var advisor = new DefaultPointcutAdvisor(pointcut, interceptor);
    advisor.setOrder(1);
    return advisor;
  }

  @RequiredArgsConstructor
  static class ActivityLogInterceptor implements MethodInterceptor {

    private final ActivityLogEventOutPort eventOutPort;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
      var result = invocation.proceed();
      try {
        var methodName = invocation.getMethod().getName();
        var action = resolveAction(methodName);
        if (Objects.nonNull(action)) {
          var targetClass = invocation.getThis().getClass();
          var entityType = resolveEntityType(targetClass);
          if (Objects.nonNull(entityType)) {
            var entityId = resolveEntityId(result, invocation.getArguments());
            var type = entityType;
            var description = buildDescription(entityType, action, entityId);
            log.debug("Activity detected: {} ({})", type, description);
            var now = LocalDateTime.now();
            var activity =
                ActivityLogDomain.builder()
                    .type(type)
                    .action(action)
                    .description(description)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();
            eventOutPort.publishLogged(activity);
          }
        }
      } catch (Exception e) {
        log.warn("Failed to log activity: {}", e.getMessage());
      }
      return result;
    }
  }

  static String resolveAction(String methodName) {
    if (methodName.startsWith("create") || methodName.startsWith("save")) return "CREATE";
    if (methodName.startsWith("update")
        || methodName.startsWith("edit")
        || methodName.startsWith("correct")) return "UPDATE";
    if (methodName.startsWith("delete")
        || methodName.startsWith("remove")
        || methodName.startsWith("approve")) return "UPDATE";
    return null;
  }

  private static final Map<String, String> ENTITY_TYPE_MAP =
      Map.of(
          "Receipt", "RECEIPT",
          "ReceiptCorrection", "RECEIPT",
          "Product", "PRODUCT",
          "Store", "STORE",
          "Price", "PRICE_RECORD",
          "Category", "CATEGORY",
          "Unit", "UNIT",
          "Alert", "ALERT",
          "FeedbackQuestion", "FEEDBACK_QUESTION");

  static String resolveEntityType(Class<?> targetClass) {
    var name = targetClass.getSimpleName();
    if (name.contains("$$")) {
      name = name.substring(0, name.indexOf("$$"));
    }
    if (name.endsWith("$")) {
      name = name.substring(0, name.length() - 1);
    }
    if (name.endsWith("ServiceImpl")) name = name.substring(0, name.length() - 5);
    if (name.endsWith("Service")) name = name.substring(0, name.length() - 7);
    if (name.endsWith("Jpa") || name.endsWith("Adapter") || name.endsWith("Aspect")) return null;
    return ENTITY_TYPE_MAP.getOrDefault(name, name);
  }

  private static String buildDescription(String entityType, String action, String entityId) {
    var desc = "%s %sd".formatted(entityType, action.toLowerCase(Locale.ROOT));
    if (Objects.nonNull(entityId)) desc = "%s (id: %s)".formatted(desc, entityId);
    return desc;
  }

  static String resolveEntityId(Object result, Object... args) {
    if (Objects.nonNull(result)) {
      var id = extractIdViaReflection(result);
      if (Objects.nonNull(id)) return id;
    }
    if (Objects.nonNull(args) && args.length > 0 && Objects.nonNull(args[0])) {
      return args[0].toString();
    }
    return null;
  }

  private static String extractIdViaReflection(Object obj) {
    try {
      var method = obj.getClass().getMethod("getId");
      var id = method.invoke(obj);
      return Objects.nonNull(id) ? id.toString() : null;
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      return null;
    }
  }
}

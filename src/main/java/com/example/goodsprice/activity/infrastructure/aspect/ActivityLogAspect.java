package com.example.goodsprice.activity.infrastructure.aspect;

import static com.example.goodsprice.activity.infrastructure.config.ActivityLogConstants.ADVISOR_ORDER;
import static com.example.goodsprice.activity.infrastructure.config.ActivityLogConstants.CLASS_PROXY_MARKER;
import static com.example.goodsprice.activity.infrastructure.config.ActivityLogConstants.CLASS_SUFFIX_ADAPTER;
import static com.example.goodsprice.activity.infrastructure.config.ActivityLogConstants.CLASS_SUFFIX_ASPECT;
import static com.example.goodsprice.activity.infrastructure.config.ActivityLogConstants.CLASS_SUFFIX_JPA;
import static com.example.goodsprice.activity.infrastructure.config.ActivityLogConstants.CLASS_SUFFIX_SERVICE;
import static com.example.goodsprice.activity.infrastructure.config.ActivityLogConstants.CLASS_SUFFIX_SERVICE_IMPL;
import static com.example.goodsprice.activity.infrastructure.config.ActivityLogConstants.DESC_FORMAT;
import static com.example.goodsprice.activity.infrastructure.config.ActivityLogConstants.DESC_WITH_ID_FORMAT;
import static com.example.goodsprice.activity.infrastructure.config.ActivityLogConstants.METHOD_GET_ID;

import com.example.goodsprice.activity.application.annotation.ActivityLog;
import com.example.goodsprice.activity.application.domain.model.ActivityLogAction;
import com.example.goodsprice.activity.application.domain.model.ActivityLogDomain;
import com.example.goodsprice.activity.application.domain.model.ActivityLogType;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ActivityLogAspect {

  private final ActivityLogEventOutPort eventOutPort;

  private static final Map<String, ActivityLogType> ENTITY_TYPE_MAP =
      Map.of(
          "Receipt", ActivityLogType.RECEIPT,
          "ReceiptCorrection", ActivityLogType.RECEIPT,
          "Product", ActivityLogType.PRODUCT,
          "Store", ActivityLogType.STORE,
          "Price", ActivityLogType.PRICE_RECORD,
          "Category", ActivityLogType.CATEGORY,
          "Unit", ActivityLogType.UNIT,
          "Alert", ActivityLogType.ALERT,
          "FeedbackQuestion", ActivityLogType.FEEDBACK_QUESTION);

  private static final String PREFIX_CREATE = "create";
  private static final String PREFIX_SAVE = "save";
  private static final String PREFIX_UPDATE = "update";
  private static final String PREFIX_EDIT = "edit";
  private static final String PREFIX_CORRECT = "correct";
  private static final String PREFIX_DELETE = "delete";
  private static final String PREFIX_REMOVE = "remove";
  private static final String PREFIX_APPROVE = "approve";

  @Bean
  public Advisor activityLogAdvisor() {
    var pointcut = AnnotationMatchingPointcut.forMethodAnnotation(ActivityLog.class);
    var interceptor = (MethodInterceptor) this::intercept;
    var advisor = new DefaultPointcutAdvisor(pointcut, interceptor);
    advisor.setOrder(ADVISOR_ORDER);
    return advisor;
  }

  private Object intercept(MethodInvocation invocation) throws Throwable {
    log.debug(
        "AOP intercept: {}.{}",
        invocation.getThis().getClass().getSimpleName(),
        invocation.getMethod().getName());
    var result = invocation.proceed();
    try {
      var methodName = invocation.getMethod().getName();
      var action = resolveAction(methodName);
      if (Objects.nonNull(action)) {
        var targetClass = invocation.getThis().getClass();
        var entityType = resolveEntityType(targetClass);
        if (Objects.nonNull(entityType)) {
          var entityId = resolveEntityId(result, invocation.getArguments());
          log.debug("Activity detected: {} ({})", entityType, action);
          var now = LocalDateTime.now();
          var activity =
              ActivityLogDomain.builder()
                  .type(entityType)
                  .action(action)
                  .description(buildDescription(entityType, action, entityId))
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

  static ActivityLogAction resolveAction(String methodName) {
    if (methodName.startsWith(PREFIX_CREATE) || methodName.startsWith(PREFIX_SAVE))
      return ActivityLogAction.CREATE;
    if (methodName.startsWith(PREFIX_UPDATE)
        || methodName.startsWith(PREFIX_EDIT)
        || methodName.startsWith(PREFIX_CORRECT)) return ActivityLogAction.UPDATE;
    if (methodName.startsWith(PREFIX_DELETE)
        || methodName.startsWith(PREFIX_REMOVE)
        || methodName.startsWith(PREFIX_APPROVE)) return ActivityLogAction.UPDATE;
    return null;
  }

  static ActivityLogType resolveEntityType(Class<?> targetClass) {
    var name = targetClass.getSimpleName();
    if (name.contains(CLASS_PROXY_MARKER)) {
      name = name.substring(0, name.indexOf(CLASS_PROXY_MARKER));
    }
    if (name.endsWith("$")) {
      name = name.substring(0, name.length() - 1);
    }
    if (name.endsWith(CLASS_SUFFIX_SERVICE_IMPL))
      name = name.substring(0, name.length() - CLASS_SUFFIX_SERVICE_IMPL.length());
    if (name.endsWith(CLASS_SUFFIX_SERVICE))
      name = name.substring(0, name.length() - CLASS_SUFFIX_SERVICE.length());
    if (name.endsWith(CLASS_SUFFIX_JPA)
        || name.endsWith(CLASS_SUFFIX_ADAPTER)
        || name.endsWith(CLASS_SUFFIX_ASPECT)) return null;
    return ENTITY_TYPE_MAP.get(name);
  }

  private static String buildDescription(
      ActivityLogType entityType, ActivityLogAction action, String entityId) {
    var desc = DESC_FORMAT.formatted(entityType, action.name().toLowerCase(Locale.ROOT));
    if (Objects.nonNull(entityId)) desc = DESC_WITH_ID_FORMAT.formatted(desc, entityId);
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
      var method = obj.getClass().getMethod(METHOD_GET_ID);
      var id = method.invoke(obj);
      return Objects.nonNull(id) ? id.toString() : null;
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      return null;
    }
  }
}

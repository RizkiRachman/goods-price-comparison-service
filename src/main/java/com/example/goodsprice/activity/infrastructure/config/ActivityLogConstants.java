package com.example.goodsprice.activity.infrastructure.config;

public final class ActivityLogConstants {

  private ActivityLogConstants() {}

  public static final int ADVISOR_ORDER = 1;

  public static final String DEFAULT_SORT_FIELD = "createdAt";
  public static final String ENTITY_FIELD_TYPE = "type";
  public static final String ENTITY_FIELD_ACTION = "action";

  public static final String ENTITY_NAME = "ActivityLog";

  public static final String METHOD_GET_ID = "getId";

  public static final String CLASS_SUFFIX_SERVICE = "Service";
  public static final String CLASS_SUFFIX_SERVICE_IMPL = "ServiceImpl";
  public static final String CLASS_SUFFIX_JPA = "Jpa";
  public static final String CLASS_SUFFIX_ADAPTER = "Adapter";
  public static final String CLASS_SUFFIX_ASPECT = "Aspect";
  public static final String CLASS_PROXY_MARKER = "$$";

  public static final String DESC_FORMAT = "%s %sd";
  public static final String DESC_WITH_ID_FORMAT = "%s (id: %s)";
}

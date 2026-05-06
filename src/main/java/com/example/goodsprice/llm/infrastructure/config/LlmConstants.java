package com.example.goodsprice.llm.infrastructure.config;

public final class LlmConstants {

  private LlmConstants() {}

  // Provider names
  public static final String PROVIDER_LOCAL = "local";
  public static final String PROVIDER_GEMINI = "gemini";
  public static final String PROVIDER_GROQ = "groq";
  public static final String PROVIDER_SUMOPOD = "sumopod";

  // Deployment types
  public static final String TYPE_LOCAL = "local";
  public static final String TYPE_CLOUD = "cloud";

  // MIME
  public static final String MIME_IMAGE_JPEG = "image/jpeg";
  public static final String DATA_IMAGE_JPEG_PREFIX = "data:image/jpeg;base64,";

  // Receipt extraction JSON keys
  public static final String KEY_STORE_NAME = "storeName";
  public static final String KEY_STORE_LOCATION = "storeLocation";
  public static final String KEY_DATE = "date";
  public static final String KEY_ITEMS = "items";
  public static final String KEY_TOTAL_AMOUNT = "totalAmount";
  public static final String KEY_PRODUCT_NAME = "productName";
  public static final String KEY_CATEGORY = "category";
  public static final String KEY_QUANTITY = "quantity";
  public static final String KEY_UNIT_PRICE = "unitPrice";
  public static final String KEY_TOTAL_PRICE = "totalPrice";
  public static final String KEY_UNIT_TYPE = "unitType";
  public static final String KEY_RAW_TEXT = "rawText";
  public static final String KEY_ERROR = "error";

  // Groq API request keys
  public static final String GENERAL_MODEL = "model";
  public static final String GENERAL_MESSAGES = "messages";
  public static final String GENERAL_MAX_TOKENS = "max_tokens";
  public static final String GENERAL_TEMPERATURE = "temperature";
  public static final String GENERAL_RESPONSE_FORMAT = "response_format";
  public static final String GENERAL_ROLE = "role";
  public static final String GENERAL_CONTENT = "content";
  public static final String GENERAL_TYPE = "type";
  public static final String GENERAL_TEXT = "text";
  public static final String GENERAL_IMAGE_URL = "image_url";
  public static final String GENERAL_URL = "url";
  public static final String GENERAL_VALUE_JSON_OBJECT = "json_object";

  // Groq API response keys
  public static final String GENERAL_CHOICES = "choices";
  public static final String GENERAL_MESSAGE = "message";

  // Gemini response parsing
  public static final String CODE_BLOCK_MARKER = "```";
  public static final String JSON_CODE_BLOCK_MARKER = "```json";

  // Mock data
  public static final String MOCK_STORE = "Mock Store";
  public static final String MOCK_DATE = "2024-01-01";
  public static final String MOCK_PRODUCT_MILK = "Milk";
  public static final String MOCK_PRODUCT_BREAD = "Bread";
}

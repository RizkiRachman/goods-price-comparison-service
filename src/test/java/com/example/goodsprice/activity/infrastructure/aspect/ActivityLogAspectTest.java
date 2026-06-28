package com.example.goodsprice.activity.infrastructure.aspect;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.example.goodsprice.activity.application.domain.model.ActivityLogAction;
import com.example.goodsprice.activity.application.domain.model.ActivityLogDomain;
import com.example.goodsprice.activity.application.domain.model.ActivityLogType;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ActivityLogAspectTest {

  private ActivityLogAspect aspect;

  @BeforeEach
  void setUp() {
    aspect = new ActivityLogAspect(null);
  }

  @Test
  @DisplayName("Should create advisor")
  void shouldCreateAdvisor() {
    var advisor = aspect.activityLogAdvisor();
    assertNotNull(advisor);
  }

  @Test
  @DisplayName("Should resolve CREATE action for create* and save*")
  void shouldResolveCreateAction() {
    assertEquals(ActivityLogAction.CREATE, ActivityLogAspect.resolveAction("createProduct"));
    assertEquals(ActivityLogAction.CREATE, ActivityLogAspect.resolveAction("saveProduct"));
    assertEquals(ActivityLogAction.CREATE, ActivityLogAspect.resolveAction("create"));
    assertEquals(ActivityLogAction.CREATE, ActivityLogAspect.resolveAction("save"));
  }

  @Test
  @DisplayName("Should resolve UPDATE action for update*/edit*/correct*")
  void shouldResolveUpdateAction() {
    assertEquals(ActivityLogAction.UPDATE, ActivityLogAspect.resolveAction("updateProduct"));
    assertEquals(ActivityLogAction.UPDATE, ActivityLogAspect.resolveAction("editProduct"));
    assertEquals(ActivityLogAction.UPDATE, ActivityLogAspect.resolveAction("correctProduct"));
    assertEquals(ActivityLogAction.UPDATE, ActivityLogAspect.resolveAction("update"));
    assertEquals(ActivityLogAction.UPDATE, ActivityLogAspect.resolveAction("edit"));
    assertEquals(ActivityLogAction.UPDATE, ActivityLogAspect.resolveAction("correct"));
  }

  @Test
  @DisplayName("Should resolve UPDATE action for delete*/remove*/approve*")
  void shouldResolveUpdateActionForDelete() {
    assertEquals(ActivityLogAction.UPDATE, ActivityLogAspect.resolveAction("deleteProduct"));
    assertEquals(ActivityLogAction.UPDATE, ActivityLogAspect.resolveAction("removeProduct"));
    assertEquals(ActivityLogAction.UPDATE, ActivityLogAspect.resolveAction("approveProduct"));
    assertEquals(ActivityLogAction.UPDATE, ActivityLogAspect.resolveAction("delete"));
    assertEquals(ActivityLogAction.UPDATE, ActivityLogAspect.resolveAction("remove"));
    assertEquals(ActivityLogAction.UPDATE, ActivityLogAspect.resolveAction("approve"));
  }

  @Test
  @DisplayName("Should return null for non-matching method names")
  void shouldReturnNullForNonMatchingNames() {
    assertNull(ActivityLogAspect.resolveAction("getProduct"));
    assertNull(ActivityLogAspect.resolveAction("findAll"));
    assertNull(ActivityLogAspect.resolveAction("search"));
    assertNull(ActivityLogAspect.resolveAction(""));
  }

  @Test
  @DisplayName("Should resolve entity type from Service suffix")
  void shouldResolveEntityTypeFromService() {
    assertEquals(
        ActivityLogType.PRODUCT, ActivityLogAspect.resolveEntityType(ProductService.class));
  }

  @Test
  @DisplayName("Should resolve entity type from ServiceImpl suffix")
  void shouldResolveEntityTypeFromServiceImpl() {
    assertEquals(
        ActivityLogType.PRODUCT, ActivityLogAspect.resolveEntityType(ProductServiceImpl.class));
  }

  @Test
  @DisplayName("Should resolve entity type from proxy class with $$")
  void shouldResolveEntityTypeFromProxy() {
    assertEquals(
        ActivityLogType.PRODUCT,
        ActivityLogAspect.resolveEntityType(ProductService$$EnhancerBySpringCGLIB.class));
  }

  @Test
  @DisplayName("Should return null for adapter, JPA, and aspect classes")
  void shouldReturnNullForNonEntityClasses() {
    assertNull(ActivityLogAspect.resolveEntityType(ProductAdapter.class));
    assertNull(ActivityLogAspect.resolveEntityType(ProductJpa.class));
    assertNull(ActivityLogAspect.resolveEntityType(ActivityLogAspect.class));
  }

  @Test
  @DisplayName("Should resolve Receipt type")
  void shouldResolveReceiptType() {
    assertEquals(
        ActivityLogType.RECEIPT, ActivityLogAspect.resolveEntityType(ReceiptService.class));
  }

  @Test
  @DisplayName("Should resolve Store type")
  void shouldResolveStoreType() {
    assertEquals(ActivityLogType.STORE, ActivityLogAspect.resolveEntityType(StoreService.class));
  }

  @Test
  @DisplayName("Should resolve Price record type")
  void shouldResolvePriceType() {
    assertEquals(
        ActivityLogType.PRICE_RECORD, ActivityLogAspect.resolveEntityType(PriceService.class));
  }

  @Test
  @DisplayName("Should resolve entity id from result with getId method")
  void shouldResolveEntityIdFromResult() {
    var domain = ActivityLogDomain.builder().id(UUID.randomUUID()).build();
    var id = ActivityLogAspect.resolveEntityId(domain);
    assertNotNull(id);
    assertEquals(domain.getId().toString(), id);
  }

  @Test
  @DisplayName("Should resolve entity id from first argument when result has no getId")
  void shouldResolveEntityIdFromArgs() {
    var id = ActivityLogAspect.resolveEntityId(null, "entity-123");
    assertEquals("entity-123", id);
  }

  @Test
  @DisplayName("Should prefer result id over argument")
  void shouldPreferResultOverArgs() {
    var domain =
        ActivityLogDomain.builder()
            .id(UUID.fromString("00000000-0000-0000-0000-000000000001"))
            .build();
    var id = ActivityLogAspect.resolveEntityId(domain, "arg-value");
    assertEquals(domain.getId().toString(), id);
  }

  @Test
  @DisplayName("Should resolve Category entity type")
  void shouldResolveCategoryEntityType() {
    assertEquals(
        ActivityLogType.CATEGORY, ActivityLogAspect.resolveEntityType(CategoryService.class));
  }

  @Test
  @DisplayName("Should resolve Unit entity type")
  void shouldResolveUnitEntityType() {
    assertEquals(ActivityLogType.UNIT, ActivityLogAspect.resolveEntityType(UnitService.class));
  }

  @Test
  @DisplayName("Should resolve Alert entity type")
  void shouldResolveAlertEntityType() {
    assertEquals(ActivityLogType.ALERT, ActivityLogAspect.resolveEntityType(AlertService.class));
  }

  @Test
  @DisplayName("Should resolve FeedbackQuestion entity type")
  void shouldResolveFeedbackQuestionEntityType() {
    assertEquals(
        ActivityLogType.FEEDBACK_QUESTION,
        ActivityLogAspect.resolveEntityType(FeedbackQuestionService.class));
  }

  @Test
  @DisplayName("Should resolve ReceiptCorrection as RECEIPT entity type")
  void shouldResolveReceiptCorrectionAsReceipt() {
    assertEquals(
        ActivityLogType.RECEIPT,
        ActivityLogAspect.resolveEntityType(ReceiptCorrectionService.class));
  }

  @Test
  @DisplayName("Should return null for unknown entity type")
  void shouldReturnNullForUnknownEntityType() {
    assertNull(ActivityLogAspect.resolveEntityType(UnknownTypeService.class));
  }

  @Test
  @DisplayName("Should strip dollar suffix from class name")
  void shouldStripDollarSuffixFromClassName() {
    assertEquals(
        ActivityLogType.PRICE_RECORD, ActivityLogAspect.resolveEntityType(PriceService$.class));
  }

  @Test
  @DisplayName("Should return null when no id source available")
  void shouldReturnNullWhenNoEntityId() {
    assertNull(ActivityLogAspect.resolveEntityId(null, (Object[]) null));
    assertNull(ActivityLogAspect.resolveEntityId(null));
    assertNull(ActivityLogAspect.resolveEntityId(null, new Object[] {null}));
  }

  @Test
  @DisplayName("Should return null for result without getId")
  void shouldReturnNullWhenNoGetIdMethod() {
    assertNull(ActivityLogAspect.resolveEntityId("plain-string"));
  }

  // --- Test support classes for entity type resolution ---

  static class ProductService {}

  static class ProductServiceImpl {}

  static class ProductAdapter {}

  static class ProductJpa {}

  static class ReceiptService {}

  static class StoreService {}

  static class PriceService {}

  static class CategoryService {}

  static class UnitService {}

  static class AlertService {}

  static class FeedbackQuestionService {}

  static class ReceiptCorrectionService {}

  static class UnknownTypeService {}

  static class PriceService$ {}
}

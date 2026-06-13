package com.example.goodsprice.category.infrastructure.adapter.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.goodsprice.api.model.Category;
import com.example.goodsprice.api.model.CategoryListResponse;
import com.example.goodsprice.api.model.CreateCategoryRequest;
import com.example.goodsprice.api.model.EntityStatus;
import com.example.goodsprice.api.model.UpdateCategoryRequest;
import com.example.goodsprice.common.exception.NotFoundException;
import com.example.goodsprice.common.web.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.jackson.nullable.JsonNullable;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class CategoryControllerWebMvcTest {

  @Mock private CategoryWebAdapter adapter;

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper =
        Jackson2ObjectMapperBuilder.json()
            .modules(new JsonNullableModule(), new JavaTimeModule())
            .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build();

    var controller = new CategoryController(adapter);
    mockMvc =
        MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(new GlobalExceptionHandler())
            .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
            .build();
  }

  private Category createApiCategory() {
    var category = new Category();
    category.setId("FOOD");
    category.setName("Food");
    category.setDescription(JsonNullable.of("Food and grocery items"));
    category.setStatus(EntityStatus.APPROVED);
    category.setCreatedAt(OffsetDateTime.parse("2026-01-01T00:00:00Z"));
    category.setUpdatedAt(OffsetDateTime.parse("2026-03-29T10:00:00Z"));
    return category;
  }

  private String toJson(Object obj) throws Exception {
    return objectMapper.writeValueAsString(obj);
  }

  @Test
  @DisplayName("POST /v1/categories should return 201 Created")
  void shouldCreateCategoryReturns201() throws Exception {
    var category = createApiCategory();
    when(adapter.create(any(CreateCategoryRequest.class))).thenReturn(category);

    var request = new CreateCategoryRequest("FOOD", "Food");
    request.setDescription("Food and grocery items");
    request.setStatus(EntityStatus.APPROVED);

    mockMvc
        .perform(
            post("/v1/categories").contentType(MediaType.APPLICATION_JSON).content(toJson(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value("FOOD"))
        .andExpect(jsonPath("$.name").value("Food"));
  }

  @Test
  @DisplayName("GET /v1/categories/{id} should return 200 OK")
  void shouldGetCategoryReturns200() throws Exception {
    var category = createApiCategory();
    when(adapter.findById("FOOD")).thenReturn(category);

    mockMvc
        .perform(get("/v1/categories/FOOD").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("FOOD"))
        .andExpect(jsonPath("$.name").value("Food"));
  }

  @Test
  @DisplayName("GET /v1/categories should return 200 OK with paginated list")
  void shouldListCategoriesReturns200() throws Exception {
    var listResponse = new CategoryListResponse();
    when(adapter.list(any(), any(), any(), any(), any(), any())).thenReturn(listResponse);

    mockMvc
        .perform(
            get("/v1/categories")
                .param("page", "1")
                .param("pageSize", "20")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("PUT /v1/categories/{id} should return 200 OK")
  void shouldUpdateCategoryReturns200() throws Exception {
    var category = createApiCategory();
    when(adapter.update(eq("FOOD"), any(UpdateCategoryRequest.class))).thenReturn(category);

    var updateRequest = new UpdateCategoryRequest();
    updateRequest.setName("Updated Food");

    mockMvc
        .perform(
            put("/v1/categories/FOOD")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(updateRequest))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("FOOD"));
  }

  @Test
  @DisplayName("GET /v1/categories/{id} should return 404 when category not found")
  void shouldReturn404WhenCategoryNotFound() throws Exception {
    when(adapter.findById("NONEXISTENT"))
        .thenThrow(new NotFoundException("CATEGORY_NOT_FOUND", "Category not found: NONEXISTENT"));

    mockMvc
        .perform(get("/v1/categories/NONEXISTENT").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("CATEGORY_NOT_FOUND"));
  }

  @Test
  @DisplayName("POST /v1/categories should return 400 when name is null")
  void shouldReturn400WhenNameIsNull() throws Exception {
    String invalidJson =
        """
        {"id": "TEST", "name": null}
        """;

    mockMvc
        .perform(
            post("/v1/categories").contentType(MediaType.APPLICATION_JSON).content(invalidJson))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("POST /v1/categories should return 400 when request body is invalid")
  void shouldReturn400WhenRequestBodyIsInvalid() throws Exception {
    mockMvc
        .perform(
            post("/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid json}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("GET /v1/categories/{id} should return correct JSON structure")
  void shouldReturnCorrectJsonStructure() throws Exception {
    var category = createApiCategory();
    when(adapter.findById("FOOD")).thenReturn(category);

    mockMvc
        .perform(get("/v1/categories/FOOD").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("FOOD"))
        .andExpect(jsonPath("$.name").value("Food"))
        .andExpect(jsonPath("$.description").value("Food and grocery items"))
        .andExpect(jsonPath("$.status").value("approved"))
        .andExpect(jsonPath("$.createdAt").exists())
        .andExpect(jsonPath("$.updatedAt").exists());
  }
}

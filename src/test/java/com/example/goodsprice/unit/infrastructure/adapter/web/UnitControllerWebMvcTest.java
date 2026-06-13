package com.example.goodsprice.unit.infrastructure.adapter.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.goodsprice.api.model.CreateUnitRequest;
import com.example.goodsprice.api.model.EntityStatus;
import com.example.goodsprice.api.model.Unit;
import com.example.goodsprice.api.model.UnitListResponse;
import com.example.goodsprice.api.model.UpdateUnitRequest;
import com.example.goodsprice.common.exception.NotFoundException;
import com.example.goodsprice.common.web.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class UnitControllerWebMvcTest {

  @Mock private UnitWebAdapter adapter;

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper =
        Jackson2ObjectMapperBuilder.json()
            .modules(new JsonNullableModule(), new JavaTimeModule())
            .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build();

    var controller = new UnitController(adapter);
    mockMvc =
        MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(new GlobalExceptionHandler())
            .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
            .build();
  }

  private Unit createApiUnit() {
    var unit = new Unit();
    unit.setId("KG");
    unit.setName("Kilogram");
    unit.setSymbol("kg");
    unit.setType(Unit.TypeEnum.WEIGHT);
    unit.setStatus(EntityStatus.APPROVED);
    return unit;
  }

  private String toJson(Object obj) throws Exception {
    return objectMapper.writeValueAsString(obj);
  }

  @Test
  @DisplayName("POST /v1/units should return 201 Created")
  void shouldCreateUnitReturns201() throws Exception {
    var unit = createApiUnit();
    when(adapter.create(any(CreateUnitRequest.class))).thenReturn(unit);

    var request = new CreateUnitRequest();
    request.setId("KG");
    request.setName("Kilogram");
    request.setSymbol("kg");
    request.setType(CreateUnitRequest.TypeEnum.WEIGHT);

    mockMvc
        .perform(post("/v1/units").contentType(MediaType.APPLICATION_JSON).content(toJson(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value("KG"))
        .andExpect(jsonPath("$.name").value("Kilogram"));
  }

  @Test
  @DisplayName("GET /v1/units/{unitId} should return 200 OK")
  void shouldGetUnitReturns200() throws Exception {
    var unit = createApiUnit();
    when(adapter.findById("KG")).thenReturn(unit);

    mockMvc
        .perform(get("/v1/units/KG").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("KG"))
        .andExpect(jsonPath("$.name").value("Kilogram"));
  }

  @Test
  @DisplayName("GET /v1/units should return 200 OK with paginated list")
  void shouldListUnitsReturns200() throws Exception {
    var listResponse = new UnitListResponse();
    when(adapter.list(any(), any(), any(), any(), any(), any(), any())).thenReturn(listResponse);

    mockMvc
        .perform(
            get("/v1/units")
                .param("page", "1")
                .param("pageSize", "20")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("PUT /v1/units/{unitId} should return 200 OK")
  void shouldUpdateUnitReturns200() throws Exception {
    var unit = createApiUnit();
    when(adapter.update(eq("KG"), any(UpdateUnitRequest.class))).thenReturn(unit);

    var updateRequest = new UpdateUnitRequest();
    updateRequest.setName("Kilogram Updated");

    mockMvc
        .perform(
            put("/v1/units/KG")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(updateRequest))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("KG"));
  }

  @Test
  @DisplayName("GET /v1/units/{unitId} should return 404 when unit not found")
  void shouldReturn404WhenUnitNotFound() throws Exception {
    when(adapter.findById("NONEXISTENT"))
        .thenThrow(new NotFoundException("UNIT_NOT_FOUND", "Unit not found with id: NONEXISTENT"));

    mockMvc
        .perform(get("/v1/units/NONEXISTENT").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("UNIT_NOT_FOUND"));
  }

  @Test
  @DisplayName("POST /v1/units should return 400 when name is null")
  void shouldReturn400WhenNameIsNull() throws Exception {
    String invalidJson =
        """
        {"id": "TEST", "name": null}
        """;

    mockMvc
        .perform(post("/v1/units").contentType(MediaType.APPLICATION_JSON).content(invalidJson))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("GET /v1/units/{unitId} should return correct JSON structure")
  void shouldReturnCorrectJsonStructure() throws Exception {
    var unit = createApiUnit();
    when(adapter.findById("KG")).thenReturn(unit);

    mockMvc
        .perform(get("/v1/units/KG").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("KG"))
        .andExpect(jsonPath("$.name").value("Kilogram"))
        .andExpect(jsonPath("$.symbol").value("kg"))
        .andExpect(jsonPath("$.type").value("WEIGHT"))
        .andExpect(jsonPath("$.status").value("approved"));
  }
}

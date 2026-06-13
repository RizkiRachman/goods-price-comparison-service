package com.example.goodsprice.receipt.infrastructure.adapter.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.goodsprice.api.model.BillSplitRequest;
import com.example.goodsprice.api.model.BillSplitResponse;
import com.example.goodsprice.api.model.ReceiptApproveResponse;
import com.example.goodsprice.api.model.ReceiptCorrectRequest;
import com.example.goodsprice.api.model.ReceiptCreateRequest;
import com.example.goodsprice.api.model.ReceiptRejectResponse;
import com.example.goodsprice.api.model.ReceiptResultResponse;
import com.example.goodsprice.api.model.ReceiptStatusResponse;
import com.example.goodsprice.api.model.ReceiptUploadResponse;
import com.example.goodsprice.common.exception.NotFoundException;
import com.example.goodsprice.common.web.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.UUID;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class ReceiptControllerWebMvcTest {

  @Mock private ReceiptWebAdapter adapter;
  @Mock private ReceiptCorrectionWebAdapter correctionAdapter;

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;
  private final UUID receiptId = UUID.randomUUID();

  @BeforeEach
  void setUp() {
    objectMapper =
        Jackson2ObjectMapperBuilder.json()
            .modules(new JsonNullableModule(), new JavaTimeModule())
            .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build();

    var controller = new ReceiptController(adapter, correctionAdapter);
    mockMvc =
        MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(new GlobalExceptionHandler())
            .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
            .build();
  }

  private String toJson(Object obj) throws Exception {
    return objectMapper.writeValueAsString(obj);
  }

  @Test
  @DisplayName("POST /v1/receipts/upload should return 202 Accepted")
  void shouldUploadReceiptReturns202() throws Exception {
    var response = new ReceiptUploadResponse().receiptId(receiptId);
    when(adapter.upload(any())).thenReturn(response);

    var mockImage =
        new MockMultipartFile("image", "receipt.jpg", "image/jpeg", "test-image".getBytes());

    mockMvc
        .perform(
            multipart("/v1/receipts/upload").file(mockImage).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isAccepted())
        .andExpect(jsonPath("$.receiptId").value(receiptId.toString()));
  }

  @Test
  @DisplayName("GET /v1/receipts/{id}/status should return 200 OK")
  void shouldGetStatusReturns200() throws Exception {
    var response = new ReceiptStatusResponse().receiptId(receiptId);
    when(adapter.getStatus(receiptId)).thenReturn(response);

    mockMvc
        .perform(get("/v1/receipts/{id}/status", receiptId).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.receiptId").value(receiptId.toString()));
  }

  @Test
  @DisplayName("GET /v1/receipts/{id}/results should return 200 OK")
  void shouldGetResultReturns200() throws Exception {
    var response = new ReceiptResultResponse().receiptId(receiptId);
    when(adapter.getResult(receiptId)).thenReturn(response);

    mockMvc
        .perform(get("/v1/receipts/{id}/results", receiptId).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.receiptId").value(receiptId.toString()));
  }

  @Test
  @DisplayName("POST /v1/receipts/{id}/approve should return 200 OK")
  void shouldApproveReceiptReturns200() throws Exception {
    var response = new ReceiptApproveResponse().receiptId(receiptId);
    when(adapter.approve(receiptId)).thenReturn(response);

    mockMvc
        .perform(post("/v1/receipts/{id}/approve", receiptId).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.receiptId").value(receiptId.toString()));
  }

  @Test
  @DisplayName("DELETE /v1/receipts/{id}/reject should return 200 OK")
  void shouldRejectReceiptReturns200() throws Exception {
    var response = new ReceiptRejectResponse().receiptId(receiptId);
    when(adapter.reject(receiptId)).thenReturn(response);

    mockMvc
        .perform(delete("/v1/receipts/{id}/reject", receiptId).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.receiptId").value(receiptId.toString()));
  }

  @Test
  @DisplayName("POST /v1/receipts/{id}/correct should return 200 OK")
  void shouldCorrectReceiptReturns200() throws Exception {
    var response = new ReceiptResultResponse().receiptId(receiptId);
    when(correctionAdapter.correct(eq(receiptId), any(ReceiptCorrectRequest.class)))
        .thenReturn(response);

    var request = new ReceiptCorrectRequest();

    mockMvc
        .perform(
            post("/v1/receipts/{id}/correct", receiptId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.receiptId").value(receiptId.toString()));
  }

  @Test
  @DisplayName("POST /v1/receipts should return 200 OK")
  void shouldCreateReceiptReturns200() throws Exception {
    var response = new ReceiptResultResponse().receiptId(receiptId);
    when(adapter.create(any(ReceiptCreateRequest.class))).thenReturn(response);

    var request = new ReceiptCreateRequest();

    mockMvc
        .perform(
            post("/v1/receipts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.receiptId").value(receiptId.toString()));
  }

  @Test
  @DisplayName("POST /v1/receipts/{receiptId}/bill-split should return 200 OK")
  void shouldSplitBillReturns200() throws Exception {
    var response = new BillSplitResponse().receiptId(receiptId);
    when(adapter.splitBill(eq(receiptId), any(BillSplitRequest.class))).thenReturn(response);

    var request = new BillSplitRequest();
    request.setType(BillSplitRequest.TypeEnum.RATIO);
    request.setNumberOfParticipants(2);

    mockMvc
        .perform(
            post("/v1/receipts/{receiptId}/bill-split", receiptId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.receiptId").value(receiptId.toString()));
  }

  @Test
  @DisplayName("GET /v1/receipts/{id}/status should return 404 when receipt not found")
  void shouldReturn404WhenReceiptNotFound() throws Exception {
    when(adapter.getStatus(receiptId))
        .thenThrow(new NotFoundException("RECEIPT_NOT_FOUND", "Receipt not found: " + receiptId));

    mockMvc
        .perform(get("/v1/receipts/{id}/status", receiptId).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("RECEIPT_NOT_FOUND"));
  }

  @Test
  @DisplayName("POST /v1/receipts should return 400 when request body is invalid")
  void shouldReturn400WhenRequestBodyIsInvalid() throws Exception {
    mockMvc
        .perform(
            post("/v1/receipts").contentType(MediaType.APPLICATION_JSON).content("{invalid json}"))
        .andExpect(status().isBadRequest());
  }
}

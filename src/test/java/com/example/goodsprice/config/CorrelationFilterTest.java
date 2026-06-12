package com.example.goodsprice.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

@ExtendWith(MockitoExtension.class)
class CorrelationFilterTest {

  private CorrelationFilter filter;

  @Mock private FilterChain filterChain;

  @BeforeEach
  void setUp() {
    filter = new CorrelationFilter();
  }

  @Test
  void shouldGenerateCorrelationIdWhenNotProvided() throws ServletException, IOException {
    var request = new MockHttpServletRequest();
    var response = new MockHttpServletResponse();

    filter.doFilterInternal(request, response, filterChain);

    String correlationId = response.getHeader("X-Correlation-ID");
    assertThat(correlationId).isNotNull().isNotBlank();
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void shouldUseExistingCorrelationIdWhenProvided() throws ServletException, IOException {
    var request = new MockHttpServletRequest();
    request.addHeader("X-Correlation-ID", "existing-id-123");
    var response = new MockHttpServletResponse();

    filter.doFilterInternal(request, response, filterChain);

    String correlationId = response.getHeader("X-Correlation-ID");
    assertThat(correlationId).isEqualTo("existing-id-123");
  }

  @Test
  void shouldPutCorrelationIdInMdc() throws ServletException, IOException {
    var request = new MockHttpServletRequest();
    var response = new MockHttpServletResponse();

    filter.doFilterInternal(request, response, filterChain);

    String mdcValue = MDC.get("correlationId");
    assertThat(mdcValue).isNotNull();
    MDC.clear();
  }
}

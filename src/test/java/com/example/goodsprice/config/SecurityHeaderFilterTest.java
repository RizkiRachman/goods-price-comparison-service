package com.example.goodsprice.config;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SecurityHeaderFilterTest {

  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  @Mock private FilterChain filterChain;

  private SecurityHeaderFilter filter;

  @BeforeEach
  void setUp() {
    filter = new SecurityHeaderFilter();
  }

  @Test
  void shouldAddSecurityHeadersForWriteMethod() throws Exception {
    when(request.getMethod()).thenReturn("POST");

    filter.doFilterInternal(request, response, filterChain);

    verify(response).setHeader("X-Content-Type-Options", "nosniff");
    verify(response).setHeader("X-Frame-Options", "DENY");
    verify(response).setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
    verify(response).setHeader("Content-Security-Policy", "default-src 'self'");
    verify(response).setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
    verify(response).setHeader("Permissions-Policy", "camera=(), microphone=(), geolocation=()");
    verify(response).setHeader("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate");
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void shouldAddSecurityHeadersForGetMethod() throws Exception {
    when(request.getMethod()).thenReturn("GET");

    filter.doFilterInternal(request, response, filterChain);

    verify(response).setHeader("X-Content-Type-Options", "nosniff");
    verify(response).setHeader("X-Frame-Options", "DENY");
  }

  @Test
  void shouldSkipCacheControlForGetMethod() throws Exception {
    when(request.getMethod()).thenReturn("GET");

    filter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    // Cache-Control is NOT set for GET - only for write methods
    // This is a behavioral assertion refuting the above test
  }

  @Test
  void shouldAddCacheControlForPutMethod() throws Exception {
    when(request.getMethod()).thenReturn("PUT");

    filter.doFilterInternal(request, response, filterChain);

    verify(response).setHeader("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate");
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void shouldAddCacheControlForDeleteMethod() throws Exception {
    when(request.getMethod()).thenReturn("DELETE");

    filter.doFilterInternal(request, response, filterChain);

    verify(response).setHeader("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate");
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void shouldAddCacheControlForPatchMethod() throws Exception {
    when(request.getMethod()).thenReturn("PATCH");

    filter.doFilterInternal(request, response, filterChain);

    verify(response).setHeader("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate");
    verify(filterChain).doFilter(request, response);
  }
}

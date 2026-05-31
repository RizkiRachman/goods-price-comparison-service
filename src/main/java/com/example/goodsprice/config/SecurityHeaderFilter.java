package com.example.goodsprice.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(1)
@ConditionalOnProperty(
    name = "security.headers.enabled",
    havingValue = "true",
    matchIfMissing = true)
public class SecurityHeaderFilter extends OncePerRequestFilter {

  private static final String X_CONTENT_TYPE_OPTIONS = "X-Content-Type-Options";
  private static final String NOSNIFF = "nosniff";
  private static final String X_FRAME_OPTIONS = "X-Frame-Options";
  private static final String DENY = "DENY";
  private static final String CACHE_CONTROL = "Cache-Control";
  private static final String NO_STORE = "no-cache, no-store, max-age=0, must-revalidate";
  private static final String STRICT_TRANSPORT_SECURITY = "Strict-Transport-Security";
  private static final String HSTS_VALUE = "max-age=31536000; includeSubDomains";
  private static final String CONTENT_SECURITY_POLICY = "Content-Security-Policy";
  private static final String CSP_VALUE = "default-src 'self'";
  private static final String REFERRER_POLICY = "Referrer-Policy";
  private static final String REFERRER_VALUE = "strict-origin-when-cross-origin";
  private static final String PERMISSIONS_POLICY = "Permissions-Policy";
  private static final String PERMISSIONS_VALUE = "camera=(), microphone=(), geolocation=()";

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    response.setHeader(X_CONTENT_TYPE_OPTIONS, NOSNIFF);
    response.setHeader(X_FRAME_OPTIONS, DENY);
    response.setHeader(STRICT_TRANSPORT_SECURITY, HSTS_VALUE);
    response.setHeader(CONTENT_SECURITY_POLICY, CSP_VALUE);
    response.setHeader(REFERRER_POLICY, REFERRER_VALUE);
    response.setHeader(PERMISSIONS_POLICY, PERMISSIONS_VALUE);

    var method = request.getMethod();
    if (isWriteMethod(method)) {
      response.setHeader(CACHE_CONTROL, NO_STORE);
    }

    filterChain.doFilter(request, response);
  }

  private static boolean isWriteMethod(String method) {
    return "POST".equals(method)
        || "PUT".equals(method)
        || "PATCH".equals(method)
        || "DELETE".equals(method);
  }
}

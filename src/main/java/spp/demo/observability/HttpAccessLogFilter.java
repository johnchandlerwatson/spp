package spp.demo.observability;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class HttpAccessLogFilter extends OncePerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(HttpAccessLogFilter.class);
    private static final Set<String> EXCLUDED_PATHS = Set.of(
        "/actuator/prometheus",
        "/actuator/health",
        "/swagger-ui/index.html",
        "/swagger-ui.html"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        long startNanos = System.nanoTime();
        String requestPath = request.getRequestURI();
        boolean skipAccessLog = EXCLUDED_PATHS.contains(requestPath);

        String requestId = request.getHeader("X-Request-Id");
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }

        response.setHeader("X-Request-Id", requestId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            if (skipAccessLog) {
                return;
            }

            long durationMs = (System.nanoTime() - startNanos) / 1_000_000L;

            MDC.put("event", "http.access");
            MDC.put("request_id", requestId);
            MDC.put("http_method", request.getMethod());
            MDC.put("http_path", requestPath);
            MDC.put("http_query", request.getQueryString() == null ? "" : request.getQueryString());
            MDC.put("http_status", Integer.toString(response.getStatus()));
            MDC.put("duration_ms", Long.toString(durationMs));
            MDC.put("client_ip", getClientIp(request));
            MDC.put("user_agent", request.getHeader("User-Agent") == null ? "" : request.getHeader("User-Agent"));

            LOG.info("http.access");
            MDC.clear();
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            int commaIndex = xForwardedFor.indexOf(',');
            return commaIndex > 0 ? xForwardedFor.substring(0, commaIndex).trim() : xForwardedFor.trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isBlank()) {
            return xRealIp.trim();
        }

        return request.getRemoteAddr();
    }
}

package com.bni.taskmgtapp.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    // Max requests allowed per IP per minute
    private static final int MAX_REQUESTS = 10;  // reduced for demo/testing
    private final Map<String, RequestStats> clientRequests = new ConcurrentHashMap<>();

    private static final Logger logger = LoggerFactory.getLogger(RateLimitInterceptor.class);


    public RateLimitInterceptor() {
        logger.info("✅ RateLimitInterceptor initialized!");
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String clientIp = getClientIP(request);
        logger.info("RateLimitInterceptor triggered for IP: {}", getClientIP(request));

        RequestStats stats = clientRequests.getOrDefault(clientIp, new RequestStats());

        Instant now = Instant.now();
        if (stats.isWithinCurrentMinute(now)) {
            if (stats.requestCount >= MAX_REQUESTS) {
                logger.warn("Rate limit exceeded for IP: {}", clientIp);
                response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                response.getWriter().write("Too many requests");
                return false;
            } else {
                stats.increment();
            }
        } else {
            stats.reset(now);
        }

        clientRequests.put(clientIp, stats);
        return true;
    }

    private String getClientIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    private static class RequestStats {
        int requestCount;
        Instant windowStart;

        public RequestStats() {
            this.windowStart = Instant.now();
            this.requestCount = 0;
        }

        public void increment() {
            requestCount++;
        }

        public void reset(Instant start) {
            this.windowStart = start;
            this.requestCount = 1;
        }

        public boolean isWithinCurrentMinute(Instant now) {
            return now.isBefore(windowStart.plusSeconds(60));
        }
    }
}
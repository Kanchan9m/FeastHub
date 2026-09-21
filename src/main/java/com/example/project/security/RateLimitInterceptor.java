package com.example.project.security;

import com.example.project.exception.RateLimitExceededException;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        String path = request.getRequestURI();
        String clientIp = request.getRemoteAddr();
        String key = clientIp + ":" + path;

        Bucket bucket = buckets.computeIfAbsent(key, k -> createBucket(path));

        if (bucket.tryConsume(1)) {
            return true;
        }
//        response.setStatus(
//                HttpServletResponse.SC_TOO_MANY_REQUESTS
//        );
//
//        response.setContentType("application/json");
        throw new RateLimitExceededException("Too many requests. Please try again later.");
    }

    private Bucket createBucket(String path) {

        if (path.equals("/rms/signin")) {
            return createBucket(5, Duration.ofSeconds(30));
        }

        if (path.equals("/rms/owner/register")) {
            return createBucket(5, Duration.ofSeconds(30));
        }
        if (path.equals("/rms/owner/verify-email")) {
            return createBucket(5, Duration.ofMinutes(5));
        }

        if (path.equals("/rms/owner/resend-verification")) {
            return createBucket(3, Duration.ofMinutes(5));
        }

        return createBucket(10, Duration.ofMinutes(1));

//    private Bucket createBucket() {
//
//        Refill refill = Refill.intervally(5, Duration.ofMinutes(1));
//
//        Bandwidth limit = Bandwidth.classic(5, refill);
//
//        return Bucket.builder().addLimit(limit).build();
//    }
    }

    private Bucket createBucket(int capacity, Duration duration) {

        Refill refill = Refill.intervally(capacity, duration);
        Bandwidth limit = Bandwidth.classic(capacity, refill);

        return Bucket.builder().addLimit(limit).build();
    }
}

package com.perf.lab.services.cache;

import com.perf.lab.dtos.GetProductResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductRedisCache {

    private static final String KEY_SUMMARY = "product:summary:";
    private static final Duration CACHE_TTL = Duration.ofMinutes(1);

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    public Optional<GetProductResponseDto> getSummary(Long id) {

        String responseJson = stringRedisTemplate.opsForValue().get(KEY_SUMMARY + id);

        if (responseJson == null) {
            log.info("Cache-Miss summary:{} ", id);
            return Optional.empty();
        }

        log.info("Cache-Hit summary:{}", id);

        try {
            GetProductResponseDto response = objectMapper.readValue(responseJson, GetProductResponseDto.class);
            return Optional.of(response);
        } catch (Exception e) {
            log.error("Error parsing product summary from cache: {}", e.getMessage());
            stringRedisTemplate.delete(KEY_SUMMARY + id);
            return Optional.empty();
        }
    }

    public void putSummary(Long id, GetProductResponseDto response) {
        try {
            stringRedisTemplate.opsForValue().set(
                    KEY_SUMMARY + id,
                    objectMapper.writeValueAsString(response),
                    CACHE_TTL);
        } catch (Exception e) {
            throw new RuntimeException("Error serializing product summary to cache: " + e.getMessage());
        }
    }
}

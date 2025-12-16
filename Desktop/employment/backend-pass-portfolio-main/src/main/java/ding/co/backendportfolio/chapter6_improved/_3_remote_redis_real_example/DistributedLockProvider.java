package ding.co.backendportfolio.chapter6_improved._3_remote_redis_real_example;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class DistributedLockProvider {

    private final RedisTemplate<String, String> redisTemplate;

    public boolean tryLock(String key, long timeoutMs) {
        Boolean success = redisTemplate.opsForValue().setIfAbsent(key, "locked", timeoutMs, TimeUnit.MILLISECONDS);
        return Boolean.TRUE.equals(success);
    }

    public void releaseLock(String key) {
        redisTemplate.delete(key);
    }
}


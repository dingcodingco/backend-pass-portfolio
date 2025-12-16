package ding.co.backendportfolio.chapter6._0_config_test;

import ding.co.backendportfolio.config.IntegrationTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@IntegrationTest
public class RedisConfigTest {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @DisplayName("레디스 컨테이너 테스트")
    @Test
    void contextLoads() {
        redisTemplate.opsForValue().set("test-key", "test-value");
        String result = redisTemplate.opsForValue().get("test-key");

        assertThat(result).isEqualTo("test-value");
    }
}

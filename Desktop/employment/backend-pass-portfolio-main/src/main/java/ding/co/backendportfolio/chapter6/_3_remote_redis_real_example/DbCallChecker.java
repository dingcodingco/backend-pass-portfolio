package ding.co.backendportfolio.chapter6._3_remote_redis_real_example;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class DbCallChecker {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss.S");

    private final String name;

    public DbCallChecker(String name) {
        this.name = name;
    }

    /**
     * key: epoch time(초 단위)
     * value: 해당 초에 발생한 DB 조회 횟수
     */
    private final Map<String, AtomicLong> dbSelectCountPerSecond = new ConcurrentHashMap<>();

    public void incrementDbSelectCount() {
        dbSelectCountPerSecond
                .computeIfAbsent(LocalTime.now().format(FORMATTER), k -> new AtomicLong(0))
                .incrementAndGet();
    }

    public void logDbCall() {
        TreeMap<String, AtomicLong> sortedMap = new TreeMap<>(Collections.reverseOrder());
        sortedMap.putAll(dbSelectCountPerSecond);

        int keySize = sortedMap.keySet().size();

        List<Long> counts = sortedMap.values().stream()
                .map(AtomicLong::get)
                .toList();

        long totalCalls = counts.stream().mapToLong(Long::longValue).sum();
        double avg = counts.stream().mapToLong(Long::longValue).average().orElse(0.0);

        double stdDev = 0.0;
        if (counts.size() > 1) {
            double mean = avg;
            double variance = counts.stream()
                    .mapToDouble(c -> Math.pow(c - mean, 2))
                    .sum() / counts.size();
            stdDev = Math.sqrt(variance);
        }

        // 변동계수(CV) = 표준편차 / 평균
        double cv = (avg != 0.0) ? stdDev / avg : 0.0;


        String stats = String.format(
                """
                        \n
                        =====================
                        [DB Call Stats]
                        %-18s %6s   // 테스트 케이스 이름,
                        %-18s %6d   // db 조회 시간 갯수(0.1초 단위)
                        %-18s %6d   // 전체 구간 동안의 총 DB 호출 횟수
                        %-18s %6.2f   // 변동계수(CV) - 평균 대비 상대적 분산 정도. 값이 클수록 요청량의 시간적 쏠림이 큼
                        =====================
                        """,
                "NAME", name,
                "Key Size:", keySize,
                "Total DB Calls:", totalCalls,
                "CV:", cv
        );

        log.info(stats);
        sortedMap.forEach((time, count) -> log.info("[{}] - {}", time, count));
    }

    public void reset() {
        dbSelectCountPerSecond.clear();
    }
}

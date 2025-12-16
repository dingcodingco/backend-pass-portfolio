package ding.co.backendportfolio.chapter5._3_data_processing;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class StreamBasicTest {

    @Test
    void testStream() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

        Stream<Integer> numberStream = numbers.stream()
                .filter(num -> {
                    // 로깅1: 각 숫자가 필터를 통과할 때 로그를 남깁니다
                    log.info("필터링: " + num);  // 로깅1
                    return num % 2 == 1;  // 홀수만 필터링
                })
                .map(num -> {
                    // 로깅2: 각 숫자가 필터를 통과할 때 로그를 남깁니다
                    log.info("매핑: " + num);  // 로깅2
                    return num * 2;  // 2를 곱하여 변환
                });

        // 아직 아무 연산도 실행되지 않았음을 보여주기 위한 로그
        log.info("[계획 수립 완료] 아직 실제 실행은 되지 않았습니다!");

        // 종결 연산을 호출하여 실제 실행
        log.info("[실행 시작] collect() 호출!");
        List<Integer> result = numberStream.collect(Collectors.toList());
        log.info("[실행 완료] 결과: " + result);
    }
}

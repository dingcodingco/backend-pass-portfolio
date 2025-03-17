package ding.co.backendportfolio.chapter2.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/chapter2/test/algorithm")
@Slf4j
@RequiredArgsConstructor
public class AlgorithmTestController {

    @GetMapping("/string/concat")
    public Map<String, Object> testStringConcat(@RequestParam int iterations) {
        Map<String, Object> result = new HashMap<>();
        long startTime;

        // 잘못된 문자열 연결 방식 (String concatenation)
        startTime = System.currentTimeMillis();
        String badResult = "";
        for (int i = 0; i < iterations; i++) {
            badResult += "test" + i;  // 매우 비효율적인 방식
        }
        result.put("badStringConcatTime", System.currentTimeMillis() - startTime);

        // TODO: 부하테스트 - 실습에서 주석 풀고 위의 코드 주석시키기
//        // 올바른 문자열 연결 방식 (StringBuilder)
//        startTime = System.currentTimeMillis();
//        StringBuilder goodResult = new StringBuilder();
//        for (int i = 0; i < iterations; i++) {
//            goodResult.append("test").append(i);
//        }
//        result.put("goodStringConcatTime", System.currentTimeMillis() - startTime);

        return result;
    }

    @GetMapping("/collection/iteration")
    public Map<String, Object> testCollectionIteration(@RequestParam(defaultValue = "100000") int size) {
        Map<String, Object> result = new HashMap<>();
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            list.add(i);
        }

        // 잘못된 리스트 순회 방식 (인덱스 접근)
        long startTime = System.currentTimeMillis();
        int badSum = 0;
        for (int i = 0; i < list.size(); i++) {  // 매번 size() 호출
            badSum += list.get(i);  // LinkedList의 경우 O(n) 접근
        }
        result.put("badIterationTime", System.currentTimeMillis() - startTime);

        // 올바른 리스트 순회 방식 (향상된 for문)
        startTime = System.currentTimeMillis();
        int goodSum = 0;
        for (int num : list) {  // 내부적으로 Iterator 사용
            goodSum += num;
        }
        result.put("goodIterationTime", System.currentTimeMillis() - startTime);

        return result;
    }

    @GetMapping("/stream/parallel")
    public Map<String, Object> testStreamParallel(@RequestParam(defaultValue = "1000000") int size) {
        Map<String, Object> result = new HashMap<>();
        List<Integer> numbers = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            numbers.add(i);
        }

        // 잘못된 병렬 스트림 사용 (작은 데이터셋에 대해)
        long startTime = System.currentTimeMillis();
        int badSum = numbers.parallelStream()  // 오버헤드가 더 큼
                .mapToInt(i -> i * 2)
                .sum();
        result.put("badParallelTime", System.currentTimeMillis() - startTime);

        // 올바른 순차 스트림 사용
        startTime = System.currentTimeMillis();
        int goodSum = numbers.stream()
                .mapToInt(i -> i * 2)
                .sum();
        result.put("goodSequentialTime", System.currentTimeMillis() - startTime);

        return result;
    }

    @GetMapping("/regex/compilation")
    public Map<String, Object> testRegexCompilation(@RequestParam(defaultValue = "10000") int iterations) {
        Map<String, Object> result = new HashMap<>();
        String testString = "test@example.com";

        // 잘못된 정규식 사용 (매번 컴파일)
        long startTime = System.currentTimeMillis();
        int badMatches = 0;
        for (int i = 0; i < iterations; i++) {
            if (testString.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {  // 매번 패턴 컴파일
                badMatches++;
            }
        }
        result.put("badRegexTime", System.currentTimeMillis() - startTime);

        // 올바른 정규식 사용 (미리 컴파일)
        startTime = System.currentTimeMillis();
        Pattern pattern = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
        int goodMatches = 0;
        for (int i = 0; i < iterations; i++) {
            if (pattern.matcher(testString).matches()) {
                goodMatches++;
            }
        }
        result.put("goodRegexTime", System.currentTimeMillis() - startTime);

        return result;
    }
}

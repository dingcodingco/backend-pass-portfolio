package ding.co.backendportfolio.chapter2.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/chapter2/test/resource")
@Slf4j
@RequiredArgsConstructor
public class ResourceIntensiveController {

    private static final List<byte[]> MEMORY_LEAK_LIST = new ArrayList<>();
    private static final Map<String, Object> CACHE = new ConcurrentHashMap<>();

    @GetMapping("/cpu/recursive")
    public Map<String, Object> extremeCPULoad(@RequestParam(defaultValue = "45") int n) {
        Map<String, Object> result = new HashMap<>();
        long startTime = System.currentTimeMillis();
        
        // 매우 비효율적인 피보나치 계산 (지수적 시간 복잡도)
        long fib = calculateFibonacciRecursive(n);
        
        result.put("fibonacci", fib);
        result.put("executionTime", System.currentTimeMillis() - startTime);
        return result;
    }

    private long calculateFibonacciRecursive(int n) {
        if (n <= 1) return n;
        return calculateFibonacciRecursive(n - 1) + calculateFibonacciRecursive(n - 2);
    }

    @GetMapping("/cpu/matrix")
    public Map<String, Object> matrixMultiplication(
            @RequestParam(defaultValue = "1000") int size) {
        Map<String, Object> result = new HashMap<>();
        long startTime = System.currentTimeMillis();

        // 큰 행렬 생성
        double[][] matrix1 = new double[size][size];
        double[][] matrix2 = new double[size][size];
        double[][] resultMatrix = new double[size][size];

        // 행렬 초기화
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix1[i][j] = random.nextDouble();
                matrix2[i][j] = random.nextDouble();
            }
        }

        // 비효율적인 행렬 곱셈 (캐시 지역성 무시)
        for (int i = 0; i < size; i++) {
            for (int k = 0; k < size; k++) {
                for (int j = 0; j < size; j++) {
                    resultMatrix[i][j] += matrix1[i][k] * matrix2[k][j];
                }
            }
        }

        result.put("matrixSize", size);
        result.put("executionTime", System.currentTimeMillis() - startTime);
        return result;
    }

    @GetMapping("/memory/leak")
    public Map<String, Object> memoryLeak(
            @RequestParam(defaultValue = "100") int mbSize,
            @RequestParam(defaultValue = "1000") int arrayCount) {
        Map<String, Object> result = new HashMap<>();
        
        // 대량의 바이트 배열 생성 및 저장
        for (int i = 0; i < arrayCount; i++) {
            byte[] largeArray = new byte[mbSize * 1024 * 1024]; // MB 단위
            Arrays.fill(largeArray, (byte) i);
            MEMORY_LEAK_LIST.add(largeArray); // 의도적인 메모리 누수
            
            // 캐시에도 저장
            CACHE.put("array_" + i, largeArray);
        }

        Runtime runtime = Runtime.getRuntime();
        result.put("totalMemory", runtime.totalMemory() / (1024 * 1024) + "MB");
        result.put("freeMemory", runtime.freeMemory() / (1024 * 1024) + "MB");
        result.put("usedMemory", (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024) + "MB");
        result.put("storedArrays", MEMORY_LEAK_LIST.size());
        
        return result;
    }

    @GetMapping("/memory/string")
    public Map<String, Object> stringMemory(
            @RequestParam(defaultValue = "1000") int iterations,
            @RequestParam(defaultValue = "10000") int stringSize) {
        Map<String, Object> result = new HashMap<>();
        List<String> strings = new ArrayList<>();
        
        // 대량의 큰 문자열 생성
        for (int i = 0; i < iterations; i++) {
            StringBuilder sb = new StringBuilder(stringSize);
            for (int j = 0; j < stringSize; j++) {
                sb.append(UUID.randomUUID());
            }
            strings.add(sb.toString().intern()); // intern()으로 문자열 풀에 강제 저장
        }

        Runtime runtime = Runtime.getRuntime();
        result.put("totalMemory", runtime.totalMemory() / (1024 * 1024) + "MB");
        result.put("freeMemory", runtime.freeMemory() / (1024 * 1024) + "MB");
        result.put("stringCount", strings.size());
        result.put("averageStringSize", strings.get(0).length());
        
        return result;
    }

    @GetMapping("/combined")
    public Map<String, Object> combinedLoad(
            @RequestParam(defaultValue = "500") int matrixSize,
            @RequestParam(defaultValue = "50") int mbSize,
            @RequestParam(defaultValue = "100") int arrayCount) {
        Map<String, Object> result = new HashMap<>();
        long startTime = System.currentTimeMillis();

        // CPU 부하 생성
        CompletableFuture<Void> cpuTask = CompletableFuture.runAsync(() -> {
            calculateFibonacciRecursive(40);
        });

        // 메모리 부하 생성
        CompletableFuture<Void> memoryTask = CompletableFuture.runAsync(() -> {
            for (int i = 0; i < arrayCount; i++) {
                byte[] largeArray = new byte[mbSize * 1024 * 1024];
                Arrays.fill(largeArray, (byte) i);
                MEMORY_LEAK_LIST.add(largeArray);
            }
        });

        // 행렬 곱셈 부하 생성
        CompletableFuture<Void> matrixTask = CompletableFuture.runAsync(() -> {
            double[][] matrix = new double[matrixSize][matrixSize];
            for (int i = 0; i < matrixSize; i++) {
                for (int j = 0; j < matrixSize; j++) {
                    matrix[i][j] = Math.sin(i) * Math.cos(j);
                }
            }
        });

        // 모든 작업 완료 대기
        CompletableFuture.allOf(cpuTask, memoryTask, matrixTask).join();

        result.put("executionTime", System.currentTimeMillis() - startTime);
        result.put("memoryUsed", (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024) + "MB");
        
        return result;
    }
}

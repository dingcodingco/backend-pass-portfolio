package ding.co.backendportfolio.chapter2.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chapter2/test/load")
@Slf4j
public class LoadTestController {

    // 메모리 누수를 시뮬레이션하기 위한 static 리스트
    private static final List<byte[]> memoryLeakList = new ArrayList<>();
    
    // 큰 객체를 생성하고 반환하는 API
    @GetMapping("/memory/large-object/{size}")
    public Map<String, Object> createLargeObject(@PathVariable int size) {
        // size MB 크기의 데이터 생성
        byte[] largeArray = new byte[size * 1024 * 1024];
        Arrays.fill(largeArray, (byte) 1);
        
        Map<String, Object> response = new HashMap<>();
        response.put("size", size + "MB");
        response.put("arrayLength", largeArray.length);
        return response;
    }
    
    // 메모리 누수를 시뮬레이션하는 API
    @GetMapping("/memory/leak/{size}")
    public String createMemoryLeak(@PathVariable int size) {
        // size MB 크기의 데이터를 static 리스트에 계속 추가
        byte[] leakData = new byte[size * 1024 * 1024];
        Arrays.fill(leakData, (byte) 1);
        memoryLeakList.add(leakData);
        
        return "Added " + size + "MB to memory. Total items: " + memoryLeakList.size();
    }
    
    // 대량의 객체를 생성하는 API
    @GetMapping("/memory/objects/{count}")
    public String createManyObjects(@PathVariable int count) {
        List<Object> objects = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            objects.add(new BigObject(1000)); // 각각 약 1KB의 객체
        }
        return "Created " + count + " objects";
    }
    
    // 큰 문자열을 생성하고 조작하는 API
    @GetMapping("/memory/string/{size}")
    public String createLargeString(@PathVariable int size) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size * 1024; i++) { // size KB의 문자열
            sb.append("Lorem ipsum dolor sit amet "); // 약 27 바이트
        }
        return "Created string of size: " + sb.length() + " characters";
    }

    @GetMapping("/fibonacci/{n}")
    public long fibonacci(@PathVariable int n) {
        if (n <= 1) return n;
        return fibonacci(n - 1) + fibonacci(n - 2);
    }

    @GetMapping("/prime/{n}")
    public List<Integer> findPrimes(@PathVariable int n) {
        List<Integer> primes = new ArrayList<>();
        for (int i = 2; i <= n; i++) {
            if (isPrime(i)) {
                primes.add(i);
            }
        }
        return primes;
    }

    @GetMapping("/matrix/{size}")
    public int[][] multiplyMatrix(@PathVariable int size) {
        int[][] matrix1 = generateMatrix(size);
        int[][] matrix2 = generateMatrix(size);
        return multiplyMatrices(matrix1, matrix2, size);
    }

    @GetMapping("/sort/{size}")
    public List<Integer> sortLargeArray(@PathVariable int size) {
        List<Integer> numbers = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            numbers.add(random.nextInt(1000000));
        }
        return numbers.stream().sorted().collect(Collectors.toList());
    }

    private boolean isPrime(int num) {
        if (num <= 1) return false;
        for (int i = 2; i <= Math.sqrt(num); i++) {
            if (num % i == 0) return false;
        }
        return true;
    }

    private int[][] generateMatrix(int size) {
        int[][] matrix = new int[size][size];
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = random.nextInt(100);
            }
        }
        return matrix;
    }

    private int[][] multiplyMatrices(int[][] matrix1, int[][] matrix2, int size) {
        int[][] result = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < size; k++) {
                    result[i][j] += matrix1[i][k] * matrix2[k][j];
                }
            }
        }
        return result;
    }

    @GetMapping("/cpu/heavy/{n}")
    public Map<String, Object> heavyCPUTask(@PathVariable int n) {
        Map<String, Object> result = new HashMap<>();
        
        // 여러 개의 무거운 연산 동시 실행
        List<Long> results = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            results.add(fibonacci(40)); // 피보나치
            results.add(calculatePrime(100000)); // 소수 계산
            results.add(sortLargeArray22(50000)); // 정렬
        }
        
        result.put("computations", results.size());
        return result;
    }

    private long calculatePrime(int n) {
        int count = 0;
        for (int i = 2; i <= n; i++) {
            boolean isPrime = true;
            for (int j = 2; j <= Math.sqrt(i); j++) {
                if (i % j == 0) {
                    isPrime = false;
                    break;
                }
            }
            if (isPrime) count++;
        }
        return count;
    }

    private long sortLargeArray22(int size) {
        int[] array = new Random().ints(size).toArray();
        Arrays.sort(array);
        return array[0];
    }

    @Data
    @AllArgsConstructor
    class BigObject {
        private byte[] data;

        public BigObject(int sizeInBytes) {
            this.data = new byte[sizeInBytes];
        }
    }
}


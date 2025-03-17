package ding.co.backendportfolio.chapter2.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class BoxingService {

    public int max1(Long loop) {
        var result = new ArrayList<Long>();
        for (int i = 0; i < loop; i++) {
            result.add((long) i);
        }

        return result.stream()
                .filter(v -> (v % 2 == 0))
                .mapToInt(Long::intValue)
                .max()
                .orElseThrow();
    }

    public int max2(Long loop) {
        var result = new ArrayList<Long>();
        for (int i = 0; i < loop; i++) {
            result.add((long) i);
        }

        return result.stream()
                .filter(v -> (v % 2 == 0))
                .mapToInt(Long::intValue)
                .max()
                .orElseThrow();
    }
}
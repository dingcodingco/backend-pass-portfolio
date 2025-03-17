package ding.co.backendportfolio.chapter5improved._3_data_processing.filter;

import ding.co.backendportfolio.chapter5.util.FilterUtil;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

import java.util.stream.IntStream;

@Service
public class FilterImprovedService {

    private static final int START_INCLUSIVE = 0;
    private static final int END_EXCLUSIVE_END = 1_000_000;

    private final Timer timer;

    public FilterImprovedService(MeterRegistry meterRegistry) {
        this.timer = Timer.builder("app.filter_improved.time")
                .description("filter_improved time")
                .publishPercentiles(0.5, 0.95)
                .register(meterRegistry);
    }

    /**
     * - 100만 ~ 200만 사이의 숫자 Steram을 생성한다.
     * - Filter Overhead 를 오름차순으로 최적화 버전
     */
    public void filter() {
//        timer.record(() -> {
        IntStream.range(START_INCLUSIVE, END_EXCLUSIVE_END)
                .filter(number -> FilterUtil.filterOverhead5())
                .filter(number -> FilterUtil.filterOverhead7())
                .filter(number -> FilterUtil.filterOverhead50())
                .filter(number -> FilterUtil.filterOverhead70())
                .sum();
//        });
    }
}

package ding.co.backendportfolio.chapter5._3_data_processing.filter;

import ding.co.backendportfolio.chapter5.util.FilterUtil;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

import java.util.stream.IntStream;

@Service
public class FilterService {

    private static final int START_INCLUSIVE = 0;
    private static final int END_EXCLUSIVE_END = 1_000_000;

    private final Timer timer;

    public FilterService(MeterRegistry meterRegistry) {
        this.timer = Timer.builder("app.filter.time")
                .description("filter time")
                .publishPercentiles(0.5, 0.95)
                .register(meterRegistry);
    }

    /**
     * - 0 ~ 100만 사이의 숫자 Steram을 생성한다.
     * - Filter Overhead 스코어를 내림차순 정렬해서 비효율적으로 만든다.
     */
    public void filter() {
//        timer.record(() -> {
        IntStream.range(START_INCLUSIVE, END_EXCLUSIVE_END)
                .filter(number -> FilterUtil.filterOverhead70())
                .filter(number -> FilterUtil.filterOverhead50())
                .filter(number -> FilterUtil.filterOverhead7())
                .filter(number -> FilterUtil.filterOverhead5())
                .sum();
//        });
    }
}

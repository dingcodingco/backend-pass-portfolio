package ding.co.backendportfolio.chapter5._2_bulk_operation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class SubwayDataProvider {

    /**
     * - 테스트 데이터 생성
     * - 10개 역 = 1Set
     * - 24시간(1440분)을 10 분 간격으로 144 개의 Set 데이터 생성
     * => 10 * 144 = 1440개의 데이터 생성
     */
    public static List<SubwayStats> createData() {
        List<SubwayStats> testData = new ArrayList<>(1440);

        LocalDateTime startTime = LocalDate.now().atStartOfDay()
                .minusDays(1L)
                .truncatedTo(ChronoUnit.HOURS);

        for (int station = 1; station <= 10; station++) {
            for (int min = 0; min < 1440; min += 10) {
                LocalDateTime recordTime = startTime.plusMinutes(min);
                SubwayStats subwayStats = new SubwayStats("역-" + station, recordTime);
                testData.add(subwayStats);
            }
        }

        return testData;
    }
}

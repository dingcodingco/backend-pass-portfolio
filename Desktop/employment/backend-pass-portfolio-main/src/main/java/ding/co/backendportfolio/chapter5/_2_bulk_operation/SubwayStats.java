package ding.co.backendportfolio.chapter5._2_bulk_operation;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;


@Getter
@NoArgsConstructor
@Table(name = "ch5_subway_stats")
@Entity
public class SubwayStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String stationName;

    private int boardingCount;    // 승차 인원

    private int exitingCount;   // 하차 인원

    private LocalDateTime time;

    public SubwayStats(String stationName, LocalDateTime time) {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        this.stationName = stationName;
        this.boardingCount = random.nextInt(1000);
        this.exitingCount = random.nextInt(1000);
        this.time = time;
    }
}
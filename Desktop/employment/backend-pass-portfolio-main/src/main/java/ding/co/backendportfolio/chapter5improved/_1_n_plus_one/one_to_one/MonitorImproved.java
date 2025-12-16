package ding.co.backendportfolio.chapter5improved._1_n_plus_one.one_to_one;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ch5_monitor_improved")
@Entity
public class MonitorImproved {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "computer_improved_id")
    private ComputerImproved computerImproved;

    void updateComputerImproved(ComputerImproved computerImproved) {
        this.computerImproved = computerImproved;
    }
}

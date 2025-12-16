package ding.co.backendportfolio.chapter5improved._1_n_plus_one.one_to_one;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ch5_computer_improved")
@Entity
public class ComputerImproved {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToOne(mappedBy = "computerImproved")
    private MonitorImproved monitorImproved;

    public ComputerImproved(String name) {
        this.name = name;
    }

    public void addMonitorImproved(MonitorImproved monitorImproved) {
        this.monitorImproved = monitorImproved;
        monitorImproved.updateComputerImproved(this);
    }
}

package ding.co.backendportfolio.chapter5._1_n_plus_one.one_to_one;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ch5_computer")
@Entity
public class Computer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToOne(mappedBy = "computer", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Monitor monitor;

    public Computer(String name) {
        this.name = name;
    }

    public void addMonitor(Monitor monitor) {
        this.monitor = monitor;
        monitor.updateComputer(this);
    }
}

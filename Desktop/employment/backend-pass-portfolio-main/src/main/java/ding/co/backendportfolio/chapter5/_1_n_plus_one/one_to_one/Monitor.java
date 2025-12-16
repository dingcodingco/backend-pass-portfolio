package ding.co.backendportfolio.chapter5._1_n_plus_one.one_to_one;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Table(name = "ch5_monitor")
@Entity
public class Monitor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "computer_id")
    private Computer computer;

    void updateComputer(Computer computer) {
        this.computer = computer;
    }
}

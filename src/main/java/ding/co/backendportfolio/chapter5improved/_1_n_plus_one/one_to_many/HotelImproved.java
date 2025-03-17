package ding.co.backendportfolio.chapter5improved._1_n_plus_one.one_to_many;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class HotelImproved {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "hotelImproved", fetch = FetchType.LAZY)
    private List<RoomImproved> roomImproveds = new ArrayList<>();

    public HotelImproved(String name) {
        this.name = name;
    }
}
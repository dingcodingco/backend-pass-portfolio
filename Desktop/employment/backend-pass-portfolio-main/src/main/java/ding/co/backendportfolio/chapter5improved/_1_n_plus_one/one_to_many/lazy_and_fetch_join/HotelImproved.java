package ding.co.backendportfolio.chapter5improved._1_n_plus_one.one_to_many.lazy_and_fetch_join;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ch5_hotel_improved")
@Entity
public class HotelImproved {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // 기본 설정이 지연로딩
    @OneToMany(mappedBy = "hotelImproved", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<RoomImproved> roomImproveds = new ArrayList<>();

    public HotelImproved(String name) {
        this.name = name;
    }

    public void addRoomImproved(RoomImproved roomImproved) {
        roomImproveds.add(roomImproved);
        roomImproved.setHotelImproved(this);
    }
}
package ding.co.backendportfolio.chapter5._1_n_plus_one.one_to_many_eager;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ch5_hotel_eager")
@Entity
public class HotelEager {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // 즉시 로딩으로 변경
    @OneToMany(mappedBy = "hotelEager", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<RoomEager> roomEagers = new ArrayList<>();

    public HotelEager(String name) {
        this.name = name;
    }

    public void addRoomEager(RoomEager roomEager) {
        roomEagers.add(roomEager);
        roomEager.setHotelEager(this);
    }
}
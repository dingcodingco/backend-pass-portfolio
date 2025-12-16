package ding.co.backendportfolio.chapter5._1_n_plus_one.one_to_many_eager;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ch5_room_eager")
@Entity
public class RoomEager {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // 기본 설정이 즉시 로딩
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hotel_id")
    private HotelEager hotelEager;

    public RoomEager(String name) {
        this.name = name;
    }

    public void setHotelEager(HotelEager hotelEager) {
        this.hotelEager = hotelEager;
    }
}


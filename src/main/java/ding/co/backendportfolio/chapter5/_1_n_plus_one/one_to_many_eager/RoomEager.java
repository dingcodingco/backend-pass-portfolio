package ding.co.backendportfolio.chapter5._1_n_plus_one.one_to_many_eager;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class RoomEager {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.EAGER) // 기본 설정이 즉시 로딩
    @JoinColumn(name = "hotel_id")
    private HotelEager hotelEager;

    public RoomEager(String name, HotelEager hotelEager) {
        this.name = name;
        this.hotelEager = hotelEager;
        hotelEager.getRoomEagers().add(this);
    }
}


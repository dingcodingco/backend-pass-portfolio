package ding.co.backendportfolio.chapter5improved._1_n_plus_one.one_to_many.lazy_and_fetch_join;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ch5_room_improved")
@Entity
public class RoomImproved {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.EAGER) // 기본 설정이 즉시 로딩
    @JoinColumn(name = "hotel_improved_id")
    private HotelImproved hotelImproved;

    public RoomImproved(String name) {
        this.name = name;
    }

    public void setHotelImproved(HotelImproved hotelImproved) {
        this.hotelImproved = hotelImproved;
    }
}

package ding.co.backendportfolio.chapter5improved._1_n_plus_one.one_to_many;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class RoomImproved {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩으로 변경
    private HotelImproved hotelImproved;

    public RoomImproved(String name, HotelImproved hotelImproved) {
        this.name = name;
        this.hotelImproved = hotelImproved;
        hotelImproved.getRoomImproveds().add(this);
    }
}

package ding.co.backendportfolio.chapter5improved._1_n_plus_one.one_to_many.batch_size;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ch5_room_batch_size")
@Entity
public class RoomBatchSize {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // 기본 설정이 즉시 로딩
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hotel_batch_size_id")
    private HotelBatchSize hotelBatchSize;

    public RoomBatchSize(String name) {
        this.name = name;
    }

    public void setHotelBatchSize(HotelBatchSize hotelBatchSize) {
        this.hotelBatchSize = hotelBatchSize;
    }
}

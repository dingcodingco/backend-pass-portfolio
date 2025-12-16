package ding.co.backendportfolio.chapter5improved._1_n_plus_one.one_to_many.batch_size;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ch5_hotel_batch_size")
@Entity
public class HotelBatchSize {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // 기본 설정이 지연로딩
    // 한 번에 2개의 호텔에 대한 room 을 한 번에 조회
    @BatchSize(size = 2)
    @OneToMany(mappedBy = "hotelBatchSize", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<RoomBatchSize> roomBatchSizes = new ArrayList<>();

    public HotelBatchSize(String name) {
        this.name = name;
    }

    public void addRoomBatchSize(RoomBatchSize roomBatchSize) {
        roomBatchSizes.add(roomBatchSize);
        roomBatchSize.setHotelBatchSize(this);
    }
}
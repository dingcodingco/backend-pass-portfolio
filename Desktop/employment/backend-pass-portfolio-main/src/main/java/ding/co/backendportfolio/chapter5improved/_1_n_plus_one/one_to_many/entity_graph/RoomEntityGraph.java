package ding.co.backendportfolio.chapter5improved._1_n_plus_one.one_to_many.entity_graph;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ch5_room_entity_graph")
@Entity
public class RoomEntityGraph {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.EAGER) // 기본 설정이 즉시 로딩
    @JoinColumn(name = "hotel_entity_graph_id")
    private HotelEntityGraph hotelEntityGraph;

    public RoomEntityGraph(String name) {
        this.name = name;
    }

    public void setHotelEntityGraph(HotelEntityGraph hotelEntityGraph) {
        this.hotelEntityGraph = hotelEntityGraph;
    }
}

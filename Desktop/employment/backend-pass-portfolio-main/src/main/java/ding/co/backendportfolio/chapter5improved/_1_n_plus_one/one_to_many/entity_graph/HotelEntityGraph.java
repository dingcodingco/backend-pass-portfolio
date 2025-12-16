package ding.co.backendportfolio.chapter5improved._1_n_plus_one.one_to_many.entity_graph;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ch5_hotel_entity_graph")
@Entity
public class HotelEntityGraph {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // 기본 설정이 지연로딩
    @OneToMany(mappedBy = "hotelEntityGraph", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<RoomEntityGraph> roomEntityGraphs = new ArrayList<>();

    public HotelEntityGraph(String name) {
        this.name = name;
    }

    public void addRoomEntityGraph(RoomEntityGraph roomEntityGraph) {
        roomEntityGraphs.add(roomEntityGraph);
        roomEntityGraph.setHotelEntityGraph(this);
    }

}

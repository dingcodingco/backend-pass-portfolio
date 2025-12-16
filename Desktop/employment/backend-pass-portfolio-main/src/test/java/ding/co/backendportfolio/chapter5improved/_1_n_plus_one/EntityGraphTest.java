package ding.co.backendportfolio.chapter5improved._1_n_plus_one;

import ding.co.backendportfolio.chapter5improved._1_n_plus_one.one_to_many.entity_graph.HotelEntityGraph;
import ding.co.backendportfolio.chapter5improved._1_n_plus_one.one_to_many.entity_graph.HotelEntityGraphRepository;
import ding.co.backendportfolio.chapter5improved._1_n_plus_one.one_to_many.entity_graph.HotelEntityGraphService;
import ding.co.backendportfolio.chapter5improved._1_n_plus_one.one_to_many.entity_graph.RoomEntityGraph;
import ding.co.backendportfolio.config.IntegrationTest;
import ding.co.backendportfolio.util.TestLogUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
public class EntityGraphTest {

    @Autowired
    private HotelEntityGraphRepository hotelEntityGraphRepository;

    @Autowired
    private HotelEntityGraphService hotelEntityGraphService;

    @AfterEach
    void cleanUp() {
        TestLogUtil.CleanStart();

        hotelEntityGraphRepository.deleteAll();

        TestLogUtil.CleanEnd();
    }

    @Test
    void testGetHotels() {
        // given
        TestLogUtil.setUpStart();
        createHotelEntityGraph("A", 2);
        createHotelEntityGraph("B", 2);
        createHotelEntityGraph("C", 2);
        createHotelEntityGraph("D", 2);
        createHotelEntityGraph("E", 2);

        TestLogUtil.setUpEnd();

        // when
        hotelEntityGraphService.getAvailableHotels();
    }

    private HotelEntityGraph createHotelEntityGraph(String hotelName, int roomCount) {
        HotelEntityGraph hotelEntityGraph = new HotelEntityGraph("Hotel " + hotelName);
        for (int i = 1; i <= roomCount; i++) {
            RoomEntityGraph roomEntityGraph = new RoomEntityGraph("Room " + i);
            hotelEntityGraph.addRoomEntityGraph(roomEntityGraph);
        }
        return hotelEntityGraphRepository.save(hotelEntityGraph);
    }
}

package ding.co.backendportfolio.chapter5improved._1_n_plus_one;

import ding.co.backendportfolio.chapter5improved._1_n_plus_one.one_to_many.lazy_and_fetch_join.HotelImproved;
import ding.co.backendportfolio.chapter5improved._1_n_plus_one.one_to_many.lazy_and_fetch_join.HotelImprovedRepository;
import ding.co.backendportfolio.chapter5improved._1_n_plus_one.one_to_many.lazy_and_fetch_join.HotelImprovedService;
import ding.co.backendportfolio.chapter5improved._1_n_plus_one.one_to_many.lazy_and_fetch_join.RoomImproved;
import ding.co.backendportfolio.config.IntegrationTest;
import ding.co.backendportfolio.util.TestLogUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class FetchJoinTest {

    @Autowired
    private HotelImprovedRepository hotelImprovedRepository;

    @Autowired
    private HotelImprovedService hotelImprovedService;

    @AfterEach
    void cleanUp() {
        TestLogUtil.CleanStart();

        hotelImprovedRepository.deleteAll();

        TestLogUtil.CleanEnd();
    }

    @DisplayName("Fetch Join 으로 조회")
    @Test
    void testGetHotelsImproved() {
        // given
        TestLogUtil.setUpStart();
        createHotelImproved("A", 2);
        createHotelImproved("B", 2);
        createHotelImproved("C", 2);
        createHotelImproved("D", 2);
        createHotelImproved("E", 2);
        TestLogUtil.setUpEnd();

        hotelImprovedService.getAvailableHotelImproveds();
    }

    private HotelImproved createHotelImproved(String hotelName, int roomCount) {
        HotelImproved hotelImproved = new HotelImproved("Hotel " + hotelName);
        for (int i = 1; i <= roomCount; i++) {
            RoomImproved roomImproved = new RoomImproved("Room " + i);
            hotelImproved.addRoomImproved(roomImproved);
        }
        return hotelImprovedRepository.save(hotelImproved);
    }
}
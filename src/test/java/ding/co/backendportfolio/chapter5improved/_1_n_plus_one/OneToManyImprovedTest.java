package ding.co.backendportfolio.chapter5improved._1_n_plus_one;

import ding.co.backendportfolio.chapter5improved._1_n_plus_one.one_to_many.*;
import ding.co.backendportfolio.config.IntegrationTest;
import ding.co.backendportfolio.util.TestLogUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@IntegrationTest
class OneToManyImprovedTest {

    @Autowired
    private HotelImprovedRepository hotelImprovedRepository;

    @Autowired
    private RoomImprovedRepository roomImprovedRepository;

    @Autowired
    private HotelImprovedService hotelImprovedService;

    @BeforeEach
    void setup() {
        TestLogUtil.setUpStart();

        HotelImproved hotelImprovedA = hotelImprovedRepository.save(new HotelImproved("HotelImproved A"));
        RoomImproved roomImprovedA1 = new RoomImproved("RoomImproved A1", hotelImprovedA);
        RoomImproved roomImprovedA2 = new RoomImproved("RoomImproved A2", hotelImprovedA);
        roomImprovedRepository.saveAll(List.of(roomImprovedA1, roomImprovedA2));

        HotelImproved hotelImprovedB = hotelImprovedRepository.save(new HotelImproved("HotelImproved B"));
        RoomImproved roomImprovedB1 = new RoomImproved("RoomImproved B1", hotelImprovedB);
        RoomImproved roomImprovedB2 = new RoomImproved("RoomImproved B2", hotelImprovedB);
        roomImprovedRepository.saveAll(List.of(roomImprovedB1, roomImprovedB2));

        HotelImproved hotelImprovedC = hotelImprovedRepository.save(new HotelImproved("HotelImproved C"));
        RoomImproved roomImprovedC1 = new RoomImproved("RoomImproved C1", hotelImprovedC);
        RoomImproved roomImprovedC2 = new RoomImproved("RoomImproved C2", hotelImprovedC);
        roomImprovedRepository.saveAll(List.of(roomImprovedC1, roomImprovedC2));

        TestLogUtil.setUpEnd();
    }

    @AfterEach
    void cleanUp() {
        TestLogUtil.CleanStart();

        roomImprovedRepository.deleteAllInBatch();
        hotelImprovedRepository.deleteAllInBatch();

        TestLogUtil.CleanEnd();
    }

    @DisplayName("Fetch Join 으로 조회")
    @Test
    void testGetHotelsImproved() {
        hotelImprovedService.getAvailableHotelImproveds();
    }
}
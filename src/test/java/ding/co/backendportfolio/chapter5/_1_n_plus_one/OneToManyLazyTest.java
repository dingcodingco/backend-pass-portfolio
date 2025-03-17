package ding.co.backendportfolio.chapter5._1_n_plus_one;

import ding.co.backendportfolio.chapter5._1_n_plus_one.one_to_many_lazy.*;
import ding.co.backendportfolio.config.IntegrationTest;
import ding.co.backendportfolio.util.TestLogUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@IntegrationTest
class OneToManyLazyTest {

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private HotelService hotelService;

    @BeforeEach
    void setup() {
        TestLogUtil.setUpStart();

        Hotel hotelA = hotelRepository.save(new Hotel("Hotel A"));
        Room roomA1 = new Room("Room A1", hotelA);
        Room roomA2 = new Room("Room A2", hotelA);
        roomRepository.saveAll(List.of(roomA1, roomA2));

        Hotel hotelB = hotelRepository.save(new Hotel("Hotel B")); // 호텔을 먼저 저장
        Room roomB1 = new Room("Room B1", hotelB);
        Room roomB2 = new Room("Room B2", hotelB);
        roomRepository.saveAll(List.of(roomB1, roomB2));

        TestLogUtil.setUpEnd();
    }

    @AfterEach
    void cleanUp() {
        TestLogUtil.CleanStart();

        roomRepository.deleteAllInBatch();
        hotelRepository.deleteAllInBatch();

        TestLogUtil.CleanEnd();
    }

    @DisplayName("OneToMany Lazy - getAvailableHotels")
    @Test
    void testGetHotels() {
        hotelService.getAvailableHotels();
    }
}
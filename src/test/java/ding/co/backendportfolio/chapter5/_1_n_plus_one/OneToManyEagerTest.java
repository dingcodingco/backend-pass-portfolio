package ding.co.backendportfolio.chapter5._1_n_plus_one;

import ding.co.backendportfolio.chapter5._1_n_plus_one.one_to_many_eager.*;
import ding.co.backendportfolio.config.IntegrationTest;
import ding.co.backendportfolio.util.TestLogUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@IntegrationTest
class OneToManyEagerTest {

    @Autowired
    private HotelEagerRepository hotelEagerRepository;

    @Autowired
    private RoomEagerRepository roomEagerRepository;

    @Autowired
    private HotelEagerService hotelEagerService;

    private Long hotelAId;

    @BeforeEach
    void setup() {
        TestLogUtil.setUpStart();

        HotelEager hotelEagerA = hotelEagerRepository.save(new HotelEager("HotelEager A"));
        hotelAId = hotelEagerA.getId();

        RoomEager roomEagerA1 = new RoomEager("RoomEager A1", hotelEagerA);
        RoomEager roomEagerA2 = new RoomEager("RoomEager A2", hotelEagerA);
        roomEagerRepository.saveAll(List.of(roomEagerA1, roomEagerA2));

        HotelEager hotelEagerB = hotelEagerRepository.save(new HotelEager("HotelEager B"));
        RoomEager roomEagerB1 = new RoomEager("RoomEager A1", hotelEagerB);
        RoomEager roomEagerB2 = new RoomEager("RoomEager A2", hotelEagerB);
        roomEagerRepository.saveAll(List.of(roomEagerB1, roomEagerB2));

        TestLogUtil.setUpEnd();
    }

    @AfterEach
    void cleanUp() {
        TestLogUtil.CleanStart();

        roomEagerRepository.deleteAllInBatch();
        hotelEagerRepository.deleteAllInBatch();

        TestLogUtil.CleanEnd();
    }

    @DisplayName("OneToMany Eager - getAvailableHotelEagers")
    @Test
    void testGetHotelEagers() {
        hotelEagerService.getAvailableHotelEagers();
    }

    @DisplayName("OneToMany Eager - hasAvailableHotelEager")
    @Test
    void testGetHotelEagerById() {
        hotelEagerService.hasAvailableHotelEager(hotelAId);
    }
}
package ding.co.backendportfolio.chapter5._1_n_plus_one;

import ding.co.backendportfolio.chapter5._1_n_plus_one.one_to_many_eager.HotelEager;
import ding.co.backendportfolio.chapter5._1_n_plus_one.one_to_many_eager.HotelEagerRepository;
import ding.co.backendportfolio.chapter5._1_n_plus_one.one_to_many_eager.HotelEagerService;
import ding.co.backendportfolio.chapter5._1_n_plus_one.one_to_many_eager.RoomEager;
import ding.co.backendportfolio.config.IntegrationTest;
import ding.co.backendportfolio.util.TestLogUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class OneToManyEagerTest {

    @Autowired
    private HotelEagerRepository hotelEagerRepository;

    @Autowired
    private HotelEagerService hotelEagerService;

    @AfterEach
    void cleanUp() {
        TestLogUtil.CleanStart();

        hotelEagerRepository.deleteAll();

        TestLogUtil.CleanEnd();
    }

    @DisplayName("OneToMany Eager - findById")
    @Test
    void testFindById() {
        // given
        TestLogUtil.setUpStart();
        HotelEager hotelEager = createHotelEager("A", 2);
        TestLogUtil.setUpEnd();

        // when
        hotelEagerService.findById(hotelEager.getId());
    }

    @DisplayName("OneToMany Eager - getAvailableHotelEagers")
    @Test
    void testGetAvailableHotelEagers() {
        // given
        TestLogUtil.setUpStart();
        createHotelEager("A", 2);
        createHotelEager("B", 2);
        TestLogUtil.setUpEnd();

        // when
        hotelEagerService.getAvailableHotelEagers();
    }

    private HotelEager createHotelEager(String hotelName, int roomCount) {
        HotelEager hotelEager = new HotelEager("Hotel " + hotelName);
        for (int i = 1; i <= roomCount; i++) {
            RoomEager roomEager = new RoomEager("Room " + i);
            hotelEager.addRoomEager(roomEager);
        }

        return hotelEagerRepository.save(hotelEager);
    }
}
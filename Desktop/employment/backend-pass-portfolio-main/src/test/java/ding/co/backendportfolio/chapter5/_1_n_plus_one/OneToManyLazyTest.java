package ding.co.backendportfolio.chapter5._1_n_plus_one;

import ding.co.backendportfolio.chapter5._1_n_plus_one.one_to_many_lazy.Hotel;
import ding.co.backendportfolio.chapter5._1_n_plus_one.one_to_many_lazy.HotelRepository;
import ding.co.backendportfolio.chapter5._1_n_plus_one.one_to_many_lazy.HotelService;
import ding.co.backendportfolio.chapter5._1_n_plus_one.one_to_many_lazy.Room;
import ding.co.backendportfolio.config.IntegrationTest;
import ding.co.backendportfolio.util.TestLogUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class OneToManyLazyTest {

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private HotelService hotelService;

    @AfterEach
    void cleanUp() {
        TestLogUtil.CleanStart();

        hotelRepository.deleteAll();

        TestLogUtil.CleanEnd();
    }

    @Test
    @DisplayName("OneToMany Lazy - findById")
    void testFindById() {
        // given
        TestLogUtil.setUpStart();
        Hotel hotel = createHotel("A", 2);
        TestLogUtil.setUpEnd();

        // when
        hotelService.findById(hotel.getId());
    }

    @DisplayName("OneToMany Lazy - getAvailableHotels")
    @Test
    void testGetAvailableHotels() {
        // given
        TestLogUtil.setUpStart();
        createHotel("A", 2);
        createHotel("B", 2);
        TestLogUtil.setUpEnd();

        // when
        hotelService.getAvailableHotels();
    }

    private Hotel createHotel(String hotelName, int roomCount) {
        Hotel hotel = new Hotel("Hotel " + hotelName);
        for (int i = 1; i <= roomCount; i++) {
            Room room = new Room("Room " + i);
            hotel.addRoom(room);
        }
        return hotelRepository.save(hotel);
    }
}
package ding.co.backendportfolio.chapter5improved._1_n_plus_one;

import ding.co.backendportfolio.chapter5improved._1_n_plus_one.one_to_many.batch_size.HotelBatchSize;
import ding.co.backendportfolio.chapter5improved._1_n_plus_one.one_to_many.batch_size.HotelBatchSizeRepository;
import ding.co.backendportfolio.chapter5improved._1_n_plus_one.one_to_many.batch_size.HotelBatchSizeService;
import ding.co.backendportfolio.chapter5improved._1_n_plus_one.one_to_many.batch_size.RoomBatchSize;
import ding.co.backendportfolio.config.IntegrationTest;
import ding.co.backendportfolio.util.TestLogUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
public class BatchSizeTest {

    @Autowired
    private HotelBatchSizeRepository hotelBatchSizeRepository;

    @Autowired
    private HotelBatchSizeService hotelBatchSizeService;

    @AfterEach
    void cleanUp() {
        TestLogUtil.CleanStart();

        hotelBatchSizeRepository.deleteAll();

        TestLogUtil.CleanEnd();
    }

    @DisplayName("OneToMany Batch Size - getAvailableHotels")
    @Test
    void testGetHotels() {
        // given
        TestLogUtil.setUpStart();
        createHotelBatchSize("A", 5);
        createHotelBatchSize("B", 5);
        createHotelBatchSize("C", 5);
        createHotelBatchSize("D", 5);
        createHotelBatchSize("E", 5);
        TestLogUtil.setUpEnd();

        hotelBatchSizeService.getAvailableHotels();
    }

    @DisplayName("OneToMany Batch Size - printTwoRoomName")
    @Test
    void printTwoRoomName() {
        // given
        TestLogUtil.setUpStart();
        createHotelBatchSize("A", 5);
        createHotelBatchSize("B", 5);
        createHotelBatchSize("C", 5);
        createHotelBatchSize("D", 5);
        createHotelBatchSize("E", 5);
        TestLogUtil.setUpEnd();

        // when
        hotelBatchSizeService.printTwoRoomName();
    }

    private HotelBatchSize createHotelBatchSize(String hotelName, int roomCount) {
        HotelBatchSize hotelBatchSize = new HotelBatchSize("Hotel " + hotelName);
        for (int i = 1; i <= roomCount; i++) {
            RoomBatchSize roomBatchSize = new RoomBatchSize("Room " + i);
            hotelBatchSize.addRoomBatchSize(roomBatchSize);
        }
        return hotelBatchSizeRepository.save(hotelBatchSize);
    }
}

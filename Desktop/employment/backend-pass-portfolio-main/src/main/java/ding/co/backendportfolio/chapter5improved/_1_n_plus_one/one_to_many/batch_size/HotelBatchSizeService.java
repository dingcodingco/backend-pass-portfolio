package ding.co.backendportfolio.chapter5improved._1_n_plus_one.one_to_many.batch_size;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class HotelBatchSizeService {

    private final HotelBatchSizeRepository hotelBatchSizeRepository;

    @Transactional(readOnly = true)
    public List<HotelBatchSize> getAvailableHotels() {
        List<HotelBatchSize> hotels = hotelBatchSizeRepository.findAll();

        return hotels.stream()
                .filter(hotel -> !hotel.getRoomBatchSizes().isEmpty())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public void printTwoRoomName() {
        List<HotelBatchSize> hotels = hotelBatchSizeRepository.findAll();

        // 첫 번째에서 2개의 호텔에 대한 room 을 로딩함.
        int firstSize = hotels.get(0).getRoomBatchSizes().size();
        System.out.println(">>> 첫 번째 로딩: " + firstSize);

        // 두 번째에서는 호텔의 room 을 가져오는 쿼리가 발생하지 않음
        int secondSize = hotels.get(1).getRoomBatchSizes().size();
        System.out.println(">>> 두 번째 로딩: " + secondSize);
    }
}

package ding.co.backendportfolio.chapter5._1_n_plus_one.one_to_many_lazy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class HotelService {

    private final HotelRepository repository;

    /**
     * # 상황
     * - @OneToMany(fetch = FetchType.LAZY)
     *
     * # 결과
     * - N+1 문제 발생
     *
     * # 이유
     * - rooms 가 lazy 로딩이기 때문에 사용시점에 추카 쿼리가 발생한다.
     * - n 개의 호텔을 가져왔다면, rooms 를 가져오는 n 개의 추가 쿼리가 발생한다.
     */
    @Transactional(readOnly = true)
    public List<Hotel> getAvailableHotels() {
        List<Hotel> hotels = repository.findAll();

        return hotels.stream()
                .filter(hotel -> !hotel.getRooms().isEmpty())
                .collect(Collectors.toList());
    }
}

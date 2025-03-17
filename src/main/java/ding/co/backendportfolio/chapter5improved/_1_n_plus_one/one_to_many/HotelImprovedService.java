package ding.co.backendportfolio.chapter5improved._1_n_plus_one.one_to_many;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class HotelImprovedService {

    private final HotelImprovedRepository hotelImprovedRepository;

    /**
     * # 상황
     * - fetch 조인을 사용
     * <p>
     * # 결과
     * - 쿼리가 한 번만 수행됨.
     * <p>
     * # 이유
     * - 조인을 이용해서 한 번에 가져온다.
     */
    @Transactional(readOnly = true)
    public List<HotelImproved> getAvailableHotelImproveds() {
        List<HotelImproved> hotelImproveds = hotelImprovedRepository.findAllWithRooms();

        return hotelImproveds.stream()
                .filter(hotel -> !hotel.getRoomImproveds().isEmpty())
                .collect(Collectors.toList());
    }
}

package ding.co.backendportfolio.chapter5improved._1_n_plus_one.one_to_many.lazy_and_fetch_join;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class HotelImprovedService {

    private final HotelImprovedRepository hotelImprovedRepository;

    @Transactional(readOnly = true)
    public List<HotelImproved> getAvailableHotelImproveds() {
        List<HotelImproved> hotelImproveds = hotelImprovedRepository.findAllWithRooms();

        return hotelImproveds.stream()
                .filter(hotel -> !hotel.getRoomImproveds().isEmpty())
                .collect(Collectors.toList());
    }
}

package ding.co.backendportfolio.chapter5improved._1_n_plus_one.one_to_many.entity_graph;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class HotelEntityGraphService {

    private final HotelEntityGraphRepository repository;

    @Transactional(readOnly = true)
    public List<HotelEntityGraph> getAvailableHotels() {
        List<HotelEntityGraph> hotels = repository.findAll();

        return hotels.stream()
                .filter(hotel -> !hotel.getRoomEntityGraphs().isEmpty())
                .collect(Collectors.toList());
    }
}

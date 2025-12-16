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

    @Transactional(readOnly = true)
    public void findById(Long id) {
        Hotel hotel = repository.findById(id).orElseThrow();
        System.out.println(hotel.getRooms().getClass()); // class org.hibernate.collection.spi.PersistentBag
        hotel.getRooms().size();
    }

    @Transactional(readOnly = true)
    public List<Hotel> getAvailableHotels() {
        List<Hotel> hotels = repository.findAll();

        return hotels.stream()
                .filter(hotel -> !hotel.getRooms().isEmpty())
                .collect(Collectors.toList());
    }
}

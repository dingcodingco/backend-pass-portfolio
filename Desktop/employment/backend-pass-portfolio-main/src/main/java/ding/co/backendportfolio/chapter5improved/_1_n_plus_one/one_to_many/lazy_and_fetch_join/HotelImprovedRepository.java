package ding.co.backendportfolio.chapter5improved._1_n_plus_one.one_to_many.lazy_and_fetch_join;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HotelImprovedRepository extends JpaRepository<HotelImproved, Long> {
    @Query("SELECT h FROM HotelImproved h JOIN FETCH h.roomImproveds")
    List<HotelImproved> findAllWithRooms();
}

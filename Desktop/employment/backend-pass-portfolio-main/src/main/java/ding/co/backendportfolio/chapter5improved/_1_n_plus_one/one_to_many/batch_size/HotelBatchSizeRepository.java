package ding.co.backendportfolio.chapter5improved._1_n_plus_one.one_to_many.batch_size;

import org.springframework.data.jpa.repository.JpaRepository;

public interface HotelBatchSizeRepository extends JpaRepository<HotelBatchSize, Long> {
}

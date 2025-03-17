package ding.co.backendportfolio.chapter5._1_n_plus_one.one_to_many_lazy;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {
}

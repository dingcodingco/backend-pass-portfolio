package ding.co.backendportfolio.chapter5._1_n_plus_one.one_to_many_eager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class HotelEagerService {

    private final HotelEagerRepository hotelEagerRepository;

    /**
     * # 변경한 내용
     * - @OneToMany(fetch = FetchType.EAGER)
     * <p>
     * # 결과
     * - 여전히 N + 1 이 발생한다.
     * <p>
     * # 이유
     * - JPQL 은 그냥 JPQL -> SQL 로 변경해줄 뿐이다.
     * - JPQL 은 Entity 에 작성되어있는 연관관계 로딩 방식을 고려하지 않는다.
     * - 따라서 일단 hotel 로 가져온 후 "eager 로딩이네?" => N 개의 쿼리가 추가발생
     * <p>
     * # 설명
     * - Lazy -> Eager 로 바꾼다고 무조건 문제가 해결되는 것이 아니다.
     * - 오히려 Eager 로 설정하면 찾아내기 더 어려울 수도 있다.
     */
    @Transactional(readOnly = true)
    public List<HotelEager> getAvailableHotelEagers() {
        List<HotelEager> hotelEagers = hotelEagerRepository.findAll();

        return hotelEagers.stream()
                .filter(hotelEager -> !hotelEager.getRoomEagers().isEmpty())
                .toList();
    }

    /**
     * # 변경한 내용
     * - @OneToMany(fetch = FetchType.EAGER)
     * - entityManger 의 findById 메소드를 사용
     * <p>
     * # 결과
     * - rooms 를 join 을 이용해서 한 번에 가져온다.
     * <p>
     * # 이유
     * - findById 는 entityMager 의 find 메소드를 사용하기 때문에 객체의 매핑관계를 고려할 수 있다.
     */
    @Transactional(readOnly = true)
    public boolean hasAvailableHotelEager(Long id) {
        HotelEager hotelEager = hotelEagerRepository.findById(id)
                .orElseThrow();

        return !hotelEager.getRoomEagers().isEmpty();
    }
}

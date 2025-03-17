package ding.co.backendportfolio.chapter4._3_event_with_lock.repository;

import ding.co.backendportfolio.chapter4._3_event_with_lock.entity.EventWithLock;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventWithLockRepository extends JpaRepository<EventWithLock, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from EventWithLock e where e.id = :id")
    Optional<EventWithLock> findByIdWithPessimisticLock(Long id);

    @Lock(LockModeType.OPTIMISTIC)
    @Query("select e from EventWithLock e where e.id = :id")
    Optional<EventWithLock> findByIdWithOptimisticLock(Long id);

    @Query(value = "SELECT GET_LOCK(:lockName, :timeoutSeconds)", nativeQuery = true)
    Integer getLock(@Param("lockName") String lockName, @Param("timeoutSeconds") int timeoutSeconds);

    @Query(value = "SELECT RELEASE_LOCK(:lockName)", nativeQuery = true)
    Integer releaseLock(@Param("lockName") String lockName);
} 
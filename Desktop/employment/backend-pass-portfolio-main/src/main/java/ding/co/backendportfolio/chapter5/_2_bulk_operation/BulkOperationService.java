package ding.co.backendportfolio.chapter5._2_bulk_operation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BulkOperationService {

    private final SubwayStatsRepository repository;

    @Transactional
    public void bulkInsert(List<SubwayStats> statsList) {
        repository.saveAll(statsList);
    }

    @Transactional
    public void bulkDelete() {
        repository.deleteAll();
    }
}
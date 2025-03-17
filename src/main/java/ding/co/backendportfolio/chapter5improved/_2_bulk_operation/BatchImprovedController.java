package ding.co.backendportfolio.chapter5improved._2_bulk_operation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class BatchImprovedController {

    private final SubwayStatBatchApplicationImproved subwayStatBatchApplicationImproved;

    @PostMapping("/batch/improved")
    public void bulkInsertImproved() {
        subwayStatBatchApplicationImproved.bulkInsertImproved();
    }

    @PostMapping("/batch/delete/improved")
    public void batch() {
        subwayStatBatchApplicationImproved.bulkDeleteImproved();
    }
}

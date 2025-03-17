package ding.co.backendportfolio.chapter5._2_bulk_operation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class BatchController {

    private final SubwayStatBatchApplication subwayStatBatchApplication;

    @PostMapping("/batch")
    public void batch() {
        subwayStatBatchApplication.bulkInsert();
    }
}

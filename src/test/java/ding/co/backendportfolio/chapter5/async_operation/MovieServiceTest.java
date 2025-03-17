package ding.co.backendportfolio.chapter5.async_operation;

import ding.co.backendportfolio.chapter5.async_operation.async.AsyncMovieService;
import ding.co.backendportfolio.chapter5.async_operation.sync.SynchronousMovieService;
import ding.co.backendportfolio.config.IntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class MovieServiceTest {

    @Autowired
    private AsyncMovieService asyncMovieService;

    @Autowired
    private SynchronousMovieService synchronousMovieService;

    private Long userNo = 1L;
    private Long movieId = 1L;

//    @DisplayName("동기 실행")
//    @RepeatedTest(value = 10)
//    void testSync() throws InterruptedException {
//        synchronousMovieService.getMovieDetail(userNo, movieId);
//    }
//
//    @DisplayName("비동기 실행")
//    @RepeatedTest(value = 10)
//    void testAsync() {
//        asyncMovieService.getMovieDetail(userNo, movieId);
//    }
}
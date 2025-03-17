package ding.co.backendportfolio.chapter2.service;

import ding.co.backendportfolio.chapter2.entity.Board;
import ding.co.backendportfolio.chapter2.entity.Category;
import ding.co.backendportfolio.chapter2.entity.Member;
import ding.co.backendportfolio.chapter2.entity.Role;
import ding.co.backendportfolio.chapter2.repository.BoardRepository;
import ding.co.backendportfolio.chapter2.repository.MemberRepository;
import ding.co.backendportfolio.config.IntegrationTest;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
class BoardServiceIntegrationTest {

    @Autowired
    private BoardService boardService;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("게시글 조회수 동시성 테스트 - 실제 DB 사용")
    void viewCountConcurrencyTest() throws InterruptedException {
        // given
        Member member = Member.builder()
                .email("test@example.com")
                .nickname("테스트유저")
                .role(Role.USER)
                .password("password")
                .build();
        memberRepository.save(member);

        Board board = Board.builder()
                .title("제목")
                .content("내용")
                .category(Category.FREE)
                .member(member)
                .build();
        Board savedBoard = boardRepository.save(board);

        int numberOfThreads = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        // when
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    boardService.getBoard(savedBoard.getId());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS);
        executorService.shutdown();

        // 영속성 컨텍스트 초기화 후 새로 조회
        entityManager.clear();
        Board updatedBoard = boardRepository.findById(savedBoard.getId()).orElseThrow();
        assertThat(updatedBoard.getViewCount()).isLessThan(numberOfThreads);
    }
}
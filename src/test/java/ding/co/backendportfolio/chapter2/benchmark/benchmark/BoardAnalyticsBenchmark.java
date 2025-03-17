package ding.co.backendportfolio.chapter2.benchmark.benchmark;

import ding.co.backendportfolio.chapter2.entity.*;
import ding.co.backendportfolio.chapter2.repository.BoardRepository;
import ding.co.backendportfolio.chapter2.repository.BoardTagRepository;
import ding.co.backendportfolio.chapter2.service.BoardAnalyticsService;
import ding.co.backendportfolio.chapter2.service.OptimizedBoardAnalyticsService;
import org.openjdk.jmh.annotations.*;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(value = 2, jvmArgs = {"-Xms2G", "-Xmx2G"})
@Warmup(iterations = 2)
@Measurement(iterations = 5)
public class BoardAnalyticsBenchmark {
    private BoardRepository boardRepository;
    private BoardTagRepository boardTagRepository;
    private Board testBoard;
    private List<Board> testBoards;

    private BoardAnalyticsService originalBoardAnalyticsService;
    private OptimizedBoardAnalyticsService optimizedBoardAnalyticsService;

    private void setId(Object entity, Long id) {
        try {
            Field idField = entity.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(entity, id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set ID using reflection", e);
        }
    }

    @Setup
    public void setup() {
        boardRepository = mock(BoardRepository.class);
        boardTagRepository = mock(BoardTagRepository.class);

        // 테스트용 멤버 생성
        Member testMember = Member.builder()
                .email("test@test.com")
                .password("testpass")
                .nickname("tester")
                .build();
        setId(testMember, 1L);

        // 테스트용 태그들 생성
        List<Tag> tags = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Tag tag = new Tag("Tag" + i);
            setId(tag, (long) (i + 1));
            tags.add(tag);
        }

        // 메인 테스트 보드 생성
        testBoard = Board.builder()
                .title("Test Title")
                .content("Test content for benchmark with sufficient length for analysis")
                .category(Category.FREE)
                .member(testMember)
                .build();
        setId(testBoard, 1L);

        // 보드 태그 설정
        Set<BoardTag> boardTags = new HashSet<>();
        for (Tag tag : tags) {
            BoardTag boardTag = new BoardTag(testBoard, tag);
            setId(boardTag, tag.getId());
            boardTags.add(boardTag);
        }
        testBoard.setBoardTags(boardTags);

        // 테스트용 보드 리스트 생성
        testBoards = createTestBoards(100, testMember, tags);

        // Mock 설정
        when(boardRepository.findById(1L)).thenReturn(Optional.of(testBoard));
        when(boardRepository.findAll()).thenReturn(testBoards);
        when(boardTagRepository.findTagsByBoardId(1L)).thenReturn(tags);

        // 원본 서비스와 최적화된 서비스 초기화
        originalBoardAnalyticsService = new BoardAnalyticsService(boardRepository, boardTagRepository);
        optimizedBoardAnalyticsService = new OptimizedBoardAnalyticsService(boardRepository, boardTagRepository);
    }

    private List<Board> createTestBoards(int count, Member member, List<Tag> tags) {
        List<Board> boards = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Board board = Board.builder()
                    .title("Test Title " + i)
                    .content("Test content " + i + " with some additional text for analysis")
                    .category(Category.FREE)
                    .member(member)
                    .build();
            setId(board, (long) (i + 2)); // ID 1은 이미 testBoard가 사용

            // 각 보드에 태그 추가
            Set<BoardTag> boardTags = new HashSet<>();
            for (Tag tag : tags.subList(0, Math.min(i % 3 + 1, tags.size()))) {
                BoardTag boardTag = new BoardTag(board, tag);
                setId(boardTag, (long) (boards.size() * tags.size() + tag.getId()));
                boardTags.add(boardTag);
            }
            board.setBoardTags(boardTags);

            boards.add(board);
        }
        return boards;
    }

    @Benchmark
    public Map<String, Object> benchmarkOriginalImplementation() {
        return originalBoardAnalyticsService.generateBoardStatistics(1L);
    }

    @Benchmark
    public Map<String, Object> benchmarkOptimizedImplementation() {
        return optimizedBoardAnalyticsService.generateBoardStatistics(1L);
    }
} 
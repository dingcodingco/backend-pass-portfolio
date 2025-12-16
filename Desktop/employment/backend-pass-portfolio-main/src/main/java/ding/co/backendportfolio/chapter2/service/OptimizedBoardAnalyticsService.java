package ding.co.backendportfolio.chapter2.service;

import ding.co.backendportfolio.chapter2.entity.Board;
import ding.co.backendportfolio.chapter2.repository.BoardRepository;
import ding.co.backendportfolio.chapter2.repository.BoardTagRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OptimizedBoardAnalyticsService {

    private final BoardRepository boardRepository;
    private final BoardTagRepository boardTagRepository;

    public Map<String, Object> generateBoardStatistics(Long boardId) {
        Map<String, Object> result = new HashMap<>();
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("Board not found"));

        // 최적화 1: 페이징을 사용하여 필요한 게시글만 로드
        List<Board> similarBoards = boardRepository.findSimilarBoards(
                boardId,
                board.getCategory(),
                PageRequest.of(0, 10)
        );

        // 최적화 2: 캐시를 사용한 유사도 계산
        List<Map<String, Object>> similarBoardResults = similarBoards.stream()
                .map(otherBoard -> {
                    double similarity = calculateContentSimilarityOptimized(
                            board.getContent(),
                            otherBoard.getContent()
                    );
                    Map<String, Object> resultMap = new HashMap<>();
                    resultMap.put("boardId", otherBoard.getId());
                    resultMap.put("similarity", similarity);
                    return resultMap;
                })
                .filter(map -> {
                    Object similarityObj = map.get("similarity");
                    if (similarityObj instanceof Number) {
                        return ((Number) similarityObj).doubleValue() > 0.5;
                    }
                    return false;
                })
                .collect(Collectors.toList());

        // 최적화 3: 태그 조합 분석 개선
        List<String> relatedTags = analyzeTagCombinationsOptimized(board);

        // 최적화 4: 효율적인 텍스트 분석
        Map<String, Integer> wordFrequency = analyzeContentWordsOptimized(board.getContent());

        result.put("similarBoards", similarBoardResults);
        result.put("relatedTags", relatedTags);
        result.put("wordFrequency", wordFrequency);
        return result;
    }

    private double calculateContentSimilarityOptimized(String content1, String content2) {
        // 최적화: 단어 기반 유사도 계산으로 변경
        Set<String> words1 = Arrays.stream(content1.toLowerCase().split("\\s+"))
                .collect(Collectors.toSet());
        Set<String> words2 = Arrays.stream(content2.toLowerCase().split("\\s+"))
                .collect(Collectors.toSet());

        Set<String> intersection = new HashSet<>(words1);
        intersection.retainAll(words2);
        Set<String> union = new HashSet<>(words1);
        union.addAll(words2);

        return (double) intersection.size() / union.size();
    }

    private List<String> analyzeTagCombinationsOptimized(Board board) {
        // 최적화: 태그 조합을 미리 계산하여 저장
        return board.getBoardTags().stream()
                .map(bt -> bt.getTag().getName())
                .collect(Collectors.toList());
    }

    private Map<String, Integer> analyzeContentWordsOptimized(String content) {
        // 최적화: 단일 패스로 단어 빈도 계산
        return Arrays.stream(content.toLowerCase().split("\\s+"))
                .collect(Collectors.groupingBy(
                        word -> word,
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                ));
    }
} 
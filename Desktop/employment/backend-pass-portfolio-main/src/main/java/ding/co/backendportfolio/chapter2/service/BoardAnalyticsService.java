package ding.co.backendportfolio.chapter2.service;

import ding.co.backendportfolio.chapter2.repository.BoardTagRepository;
import ding.co.backendportfolio.chapter2.entity.Board;
import ding.co.backendportfolio.chapter2.repository.BoardRepository;
import ding.co.backendportfolio.global.error.exception.EntityNotFoundException;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardAnalyticsService {
    private final BoardRepository boardRepository;
    private final BoardTagRepository boardTagRepository;
    private static final Map<String, List<String>> CONTENT_CACHE = new ConcurrentHashMap<>();

    public Map<String, Object> generateBoardStatistics(Long boardId) {
        Map<String, Object> result = new HashMap<>();
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("Board not found"));

        // 비효율적인 구현: 전체 게시글을 메모리에 로드
        List<Board> allBoards = boardRepository.findAll();
        
        // 비효율적인 구현: 중첩 루프로 유사도 계산
        List<Map<String, Object>> similarBoards = new ArrayList<>();
        for (Board otherBoard : allBoards) {
            if (!otherBoard.getId().equals(boardId)) {
                double similarity = calculateContentSimilarity(
                    board.getContent(), 
                    otherBoard.getContent()
                );
                if (similarity > 0.5) {
                    similarBoards.add(Map.of(
                        "boardId", otherBoard.getId(),
                        "similarity", similarity
                    ));
                }
            }
        }
        
        // 비효율적인 구현: 모든 태그 조합 분석
        List<String> relatedTags = analyzeTagCombinations(board);
        
        // 비효율적인 구현: 전체 텍스트 분석
        Map<String, Integer> wordFrequency = analyzeContentWords(board.getContent());
        
        result.put("similarBoards", similarBoards);
        result.put("relatedTags", relatedTags);
        result.put("wordFrequency", wordFrequency);
        return result;
    }

    private double calculateContentSimilarity(String content1, String content2) {
        // 비효율적인 구현: 모든 가능한 부분 문자열 비교
        Set<String> substrings1 = generateSubstrings(content1);
        Set<String> substrings2 = generateSubstrings(content2);
        
        int commonCount = 0;
        for (String sub1 : substrings1) {
            if (substrings2.contains(sub1)) {
                commonCount++;
            }
        }
        
        return (double) commonCount / (substrings1.size() + substrings2.size());
    }

    private Set<String> generateSubstrings(String content) {
        Set<String> substrings = new HashSet<>();
        // 비효율적인 구현: 모든 길이의 부분 문자열 생성
        for (int i = 0; i < content.length(); i++) {
            for (int j = i + 3; j <= content.length(); j++) {
                substrings.add(content.substring(i, j));
            }
        }
        return substrings;
    }

    private List<String> analyzeTagCombinations(Board board) {
        // 비효율적인 구현: 재귀적으로 모든 태그 조합 생성
        List<String> allTags = board.getBoardTags().stream()
                .map(bt -> bt.getTag().getName())
                .collect(Collectors.toList());
        
        return generateTagCombinations(allTags, 0, new ArrayList<>());
    }

    private List<String> generateTagCombinations(List<String> tags, int start, List<String> current) {
        List<String> result = new ArrayList<>();
        if (!current.isEmpty()) {
            result.add(String.join("-", current));
        }
        
        for (int i = start; i < tags.size(); i++) {
            current.add(tags.get(i));
            result.addAll(generateTagCombinations(tags, i + 1, current));
            current.remove(current.size() - 1);
        }
        
        return result;
    }

    private Map<String, Integer> analyzeContentWords(String content) {
        // 비효율적인 구현: 문자열 분할 및 반복적인 문자열 연산
        String[] words = content.split("\\s+");
        Map<String, Integer> frequency = new HashMap<>();
        
        for (String word : words) {
            StringBuilder processed = new StringBuilder();
            // 비효율적인 문자열 처리
            for (char c : word.toCharArray()) {
                processed.append(Character.toLowerCase(c));
            }
            
            // 캐시 없이 매번 처리
            CONTENT_CACHE.computeIfAbsent(processed.toString(), k -> new ArrayList<>())
                        .add(word);
            
            frequency.merge(processed.toString(), 1, Integer::sum);
        }
        
        return frequency;
    }
} 
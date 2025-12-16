package ding.co.backendportfolio.chapter2.controller;

import ding.co.backendportfolio.chapter2.entity.Category;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ding.co.backendportfolio.chapter2.dto.BoardResponseDto;
import ding.co.backendportfolio.chapter2.service.BoardService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/chapter2/test/performance")
@Slf4j
@RequiredArgsConstructor
public class PerformanceTestController {

    private final BoardService boardService;
    
    @GetMapping("/search/complex")
    public Map<String, Object> complexSearch(
            @RequestParam String keyword,
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) List<String> tags,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
            
        long startTime = System.currentTimeMillis();
        Map<String, Object> result = new HashMap<>();
        
        // 1. 키워드로 게시글 검색
        Page<BoardResponseDto> keywordResults = boardService.searchBoards(keyword, PageRequest.of(page, size));
        result.put("keywordSearchTime", System.currentTimeMillis() - startTime);
        result.put("keywordResults", keywordResults);
        
        // 2. 카테고리 필터링
        startTime = System.currentTimeMillis();
        Page<BoardResponseDto> categoryResults = boardService.getBoardList(category, PageRequest.of(page, size));
        result.put("categoryFilterTime", System.currentTimeMillis() - startTime);
        result.put("categoryResults", categoryResults);
        
        // 3. 태그 기반 검색 (태그가 제공된 경우에만)
        if (tags != null && !tags.isEmpty()) {
            startTime = System.currentTimeMillis();
            List<BoardResponseDto> tagResults = boardService.searchBoardsByTags(tags, PageRequest.of(page, size));
            result.put("tagSearchTime", System.currentTimeMillis() - startTime);
            result.put("tagResults", tagResults);
        }
        
        return result;
    }
}

package ding.co.backendportfolio.chapter2.controller;

import ding.co.backendportfolio.chapter2.dto.BoardRequestDto;
import ding.co.backendportfolio.chapter2.dto.BoardResponseDto;
import ding.co.backendportfolio.chapter2.entity.Category;
import ding.co.backendportfolio.chapter2.service.BoardAnalyticsService;
import ding.co.backendportfolio.chapter2.service.BoardService;
import ding.co.backendportfolio.chapter2.service.BoxingService;
import ding.co.backendportfolio.chapter2.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chapter2/boards")
public class BoardController {
    private final BoardService boardService;
    private final BoardAnalyticsService boardAnalyticsService;
    private final MemberService memberService;
    private final BoxingService boxingService;


    @PostMapping
    public ResponseEntity<BoardResponseDto> createBoard(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody BoardRequestDto requestDto) {
        Long memberId = memberService.getCurrentMember(user.getUsername()).getId();
        return ResponseEntity.ok(boardService.createBoard(memberId, requestDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardResponseDto> getBoard(@PathVariable Long id) {
        return ResponseEntity.ok(boardService.getBoard(id));
    }

    @GetMapping
    public ResponseEntity<Page<BoardResponseDto>> getBoardList(
            @RequestParam(required = false) Category category,
            Pageable pageable) {
        return ResponseEntity.ok(boardService.getBoardList(category, pageable));
    }

    @GetMapping("/boxing")
    int boxing() {
        int result1 = boxingService.max1(100000000L);
        int result2 = boxingService.max2(100000000L);
        return result1 + result2;
    }

    @PutMapping("/{id}")
    public ResponseEntity<BoardResponseDto> updateBoard(
            @PathVariable Long id,
            @AuthenticationPrincipal User user,
            @Valid @RequestBody BoardRequestDto requestDto) {
        Long memberId = memberService.getCurrentMember(user.getUsername()).getId();
        return ResponseEntity.ok(boardService.updateBoard(id, memberId, requestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        Long memberId = memberService.getCurrentMember(user.getUsername()).getId();
        boardService.deleteBoard(id, memberId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Void> toggleLike(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        Long memberId = memberService.getCurrentMember(user.getUsername()).getId();
        boardService.toggleLike(id, memberId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/tags")
    public ResponseEntity<Void> addTags(
            @PathVariable Long id,
            @RequestBody List<String> tagNames) {
        boardService.addTags(id, tagNames);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<Page<BoardResponseDto>> searchBoards(
            @RequestParam String keyword,
            Pageable pageable) {
        return ResponseEntity.ok(boardService.searchBoards(keyword, pageable));
    }

    @GetMapping("/search/complex")
    public ResponseEntity<Page<BoardResponseDto>> searchBoardsComplex(
            @RequestParam String keyword,
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) List<String> tags,
            Pageable pageable) {
        return ResponseEntity.ok(boardService.searchBoardsComplex(keyword, category, tags, pageable));
    }

    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> getBoardAnalytics(
            @RequestParam(required = false) Integer days) {
        return ResponseEntity.ok(boardService.getBoardAnalytics(days));
    }

    @GetMapping("/tags/{tagName}")
    public ResponseEntity<List<BoardResponseDto>> getBoardsByTag(
            @PathVariable String tagName) {
        return ResponseEntity.ok(boardService.getBoardsByTag(tagName));
    }

    @GetMapping("/api/chapter2/boards/{boardId}/analytics")
    public ResponseEntity<Map<String, Object>> getBoardAnalytics(@PathVariable Long boardId) {
        return ResponseEntity.ok(boardAnalyticsService.generateBoardStatistics(boardId));
    }
}
package ding.co.backendportfolio.chapter2.service;

import ding.co.backendportfolio.chapter2.repository.BoardTagRepository;
import ding.co.backendportfolio.chapter2.entity.Member;
import ding.co.backendportfolio.chapter2.dto.BoardRequestDto;
import ding.co.backendportfolio.chapter2.dto.BoardResponseDto;
import ding.co.backendportfolio.chapter2.repository.BoardLikeRepository;
import ding.co.backendportfolio.chapter2.repository.BoardRepository;
import ding.co.backendportfolio.chapter2.repository.TagRepository;
import ding.co.backendportfolio.chapter2.repository.MemberRepository;
import ding.co.backendportfolio.chapter2.entity.*;
import ding.co.backendportfolio.global.error.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final BoardLikeRepository boardLikeRepository;
    private final TagRepository tagRepository;
    private final BoardTagRepository boardTagRepository;

    @Transactional
    public BoardResponseDto createBoard(Long memberId, BoardRequestDto requestDto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));

        Board board = Board.builder()
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .category(requestDto.getCategory())
                .member(member)
                .build();

        board = boardRepository.save(board);

        if (requestDto.getTags() != null && !requestDto.getTags().isEmpty()) {
            for (String tagName : requestDto.getTags()) {
                Tag tag = tagRepository.findByName(tagName)
                        .orElseGet(() -> tagRepository.save(new Tag(tagName)));
                BoardTag boardTag = new BoardTag(board, tag);
                boardTagRepository.save(boardTag);
                board.getBoardTags().add(boardTag);
            }
        }

        return new BoardResponseDto(board);
    }

    @Transactional
    public BoardResponseDto getBoard(Long id) {
        System.out.println("kkk");
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Board not found"));
        
        board.increaseViewCount();
        boardRepository.save(board);
        
        return new BoardResponseDto(board);
    }

    public Page<BoardResponseDto> getBoardList(Category category, Pageable pageable) {
        Page<Board> boards = category == null ? 
                boardRepository.findAll(pageable) : 
                boardRepository.findByCategory(category, pageable);
                
        return boards.map(BoardResponseDto::new);
    }

    @Transactional
    public BoardResponseDto updateBoard(Long id, Long memberId, BoardRequestDto requestDto) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Board not found"));

        if (!board.getMember().getId().equals(memberId)) {
            throw new IllegalStateException("권한이 없습니다");
        }

        // 기본 정보 업데이트
        board.update(requestDto.getTitle(), requestDto.getContent(), requestDto.getCategory());

        // 태그 업데이트
        Set<Tag> newTags = requestDto.getTags().stream()
                .map(tagName -> tagRepository.findByName(tagName)
                        .orElseGet(() -> tagRepository.save(new Tag(tagName))))
                .collect(Collectors.toSet());
        
        board.updateTags(newTags);

        return new BoardResponseDto(board);
    }

    @Transactional
    public void deleteBoard(Long id, Long memberId) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Board not found"));

        if (!board.getMember().getId().equals(memberId)) {
            throw new IllegalStateException("권한이 없습니다");
        }

        boardRepository.delete(board);
    }

    @Transactional
    public void toggleLike(Long boardId, Long memberId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다"));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다"));

        Optional<BoardLike> existingLike = boardLikeRepository.findByBoardAndMember(board, member);
        if (existingLike.isPresent()) {
            boardLikeRepository.delete(existingLike.get());
        } else {
            boardLikeRepository.save(new BoardLike(member, board));
        }
    }

    @Transactional
    public void addTags(Long boardId, List<String> tagNames) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다"));

        for (String tagName : tagNames) {
            Tag tag = tagRepository.findByName(tagName)
                    .orElseGet(() -> tagRepository.save(new Tag(tagName)));
            
            if (!boardTagRepository.existsByBoardAndTag(board, tag)) {
                boardTagRepository.save(new BoardTag(board, tag));
            }
        }
    }

    public Page<BoardResponseDto> searchBoards(String keyword, Pageable pageable) {
        Page<Board> boards = boardRepository.findByTitleContainingOrContentContaining(
                keyword, keyword, pageable);
        return boards.map(BoardResponseDto::new);
    }

    public List<BoardResponseDto> getBoardsByTag(String tagName) {
        return boardRepository.findByBoardTags_Tag_Name(tagName)
                .stream()
                .map(BoardResponseDto::new)
                .collect(Collectors.toList());
    }

    public Page<BoardResponseDto> searchBoardsComplex(
            String keyword, 
            Category category, 
            List<String> tags, 
            Pageable pageable) {
        
        long tagCount = tags != null ? tags.size() : 0;
        Page<Board> boards = boardRepository.searchBoardsComplex(
                keyword, 
                category, 
                tags, 
                tagCount,
                pageable);
        
        return boards.map(BoardResponseDto::new);
    }

    public Map<String, Object> getBoardAnalytics(Integer days) {
        LocalDateTime startDate = days != null ?
                LocalDateTime.now().minusDays(days) : 
                LocalDateTime.now().minusYears(1);

        List<Map<String, Object>> dailyStats = boardRepository.getBoardAnalytics(startDate);

        // 전체 통계 계산
        Map<String, Object> result = new HashMap<>();
        result.put("dailyStats", dailyStats);
        
        // 총계 계산
        int totalPosts = dailyStats.stream()
                .mapToInt(stat -> ((Number) stat.get("postCount")).intValue())
                .sum();
        
        double avgPostsPerDay = dailyStats.stream()
                .mapToInt(stat -> ((Number) stat.get("postCount")).intValue())
                .average()
                .orElse(0.0);
        
        double avgLikesPerPost = dailyStats.stream()
                .mapToDouble(stat -> ((Number) stat.get("avgLikes")).doubleValue())
                .average()
                .orElse(0.0);
        
        int totalUniqueAuthors = dailyStats.stream()
                .mapToInt(stat -> ((Number) stat.get("uniqueAuthors")).intValue())
                .sum();

        // 종합 통계 추가
        result.put("summary", Map.of(
                "totalPosts", totalPosts,
                "avgPostsPerDay", avgPostsPerDay,
                "avgLikesPerPost", avgLikesPerPost,
                "totalUniqueAuthors", totalUniqueAuthors
        ));

        // 카테고리별 통계
        Map<Category, Long> categoryStats = boardRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(
                        Board::getCategory,
                        Collectors.counting()
                ));
        result.put("categoryStats", categoryStats);

        // 인기 태그 통계 (상위 10개)
        List<Map<String, Object>> popularTags = boardTagRepository.findPopularTags(10);
        result.put("popularTags", popularTags);

        return result;
    }

    // 추가: 복잡한 태그 기반 검색
    public Page<BoardResponseDto> searchByTagsWithStats(List<String> tags, Pageable pageable) {
        Page<Board> boards = boardRepository.findByBoardTags_Tag_NameIn(tags, pageable);
        
        return boards.map(board -> {
            BoardResponseDto dto = new BoardResponseDto(board);
            
            Map<String, Long> tagStats = board.getBoardTags().stream()
                    .collect(Collectors.groupingBy(
                            bt -> bt.getTag().getName(),
                            Collectors.counting()
                    ));
            
            // 관련 태그는 해당 게시글의 모든 태그를 가져옴
            Set<String> boardTags = boardTagRepository
                    .findTagsByBoardId(board.getId()).stream()
                    .map(Tag::getName)
                    .collect(Collectors.toSet());
            
            dto.setTagStats(tagStats);
            dto.setRelatedTags(boardTags);
            
            return dto;
        });
    }

    public List<BoardResponseDto> searchBoardsByTags(List<String> tags, Pageable pageable) {
        // 태그 이름들로 게시글 검색
        Page<Board> boards = boardRepository.findByBoardTags_Tag_NameIn(tags, pageable);
        
        // 검색된 게시글들을 DTO로 변환
        return boards.getContent().stream()
                .map(board -> {
                    BoardResponseDto dto = new BoardResponseDto(board);
                    
                    // 게시글의 태그 정보 설정
                    Set<String> boardTags = board.getBoardTags().stream()
                            .map(boardTag -> boardTag.getTag().getName())
                            .collect(Collectors.toSet());
                    
                    dto.setRelatedTags(boardTags);
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
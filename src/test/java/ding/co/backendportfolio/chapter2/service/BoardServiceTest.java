package ding.co.backendportfolio.chapter2.service;

import ding.co.backendportfolio.chapter2.dto.BoardRequestDto;
import ding.co.backendportfolio.chapter2.dto.BoardResponseDto;
import ding.co.backendportfolio.chapter2.entity.Board;
import ding.co.backendportfolio.chapter2.entity.Category;
import ding.co.backendportfolio.chapter2.entity.Tag;
import ding.co.backendportfolio.chapter2.repository.BoardLikeRepository;
import ding.co.backendportfolio.chapter2.repository.BoardRepository;
import ding.co.backendportfolio.chapter2.repository.BoardTagRepository;
import ding.co.backendportfolio.chapter2.repository.TagRepository;
import ding.co.backendportfolio.chapter2.service.BoardService;
import ding.co.backendportfolio.chapter2.entity.Member;
import ding.co.backendportfolio.chapter2.entity.Role;
import ding.co.backendportfolio.chapter2.repository.MemberRepository;
import ding.co.backendportfolio.global.error.exception.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {

    @InjectMocks
    private BoardService boardService;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private BoardLikeRepository boardLikeRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private BoardTagRepository boardTagRepository;

    private Board createTestBoard(Long id, Member member) {
        Board board = Board.builder()
                .title("제목")
                .content("내용")
                .category(Category.FREE)
                .member(member)
                .build();
        
        ReflectionTestUtils.setField(board, "id", id);
        
        return board;
    }

    private Member createTestMember(Long id) {
        Member member = Member.builder()
                .email("test@example.com")
                .nickname("테스트유저")
                .role(Role.USER)
                .build();
        
        ReflectionTestUtils.setField(member, "id", id);
        
        return member;
    }

    @Test
    @DisplayName("게시글 생성 성공")
    void createBoardSuccess() {
        // given
        Member member = Member.builder()
                .email("test@example.com")
                .nickname("테스트유저")
                .role(Role.USER)
                .build();

        BoardRequestDto requestDto = BoardRequestDto.builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .category(Category.FREE)
                .tags(Arrays.asList("Spring", "Java"))
                .build();

        // 태그 엔티티 준비
        Tag springTag = new Tag("Spring");
        Tag javaTag = new Tag("Java");
        
        // tagRepository 동작 정의
        given(tagRepository.findByName("Spring")).willReturn(Optional.of(springTag));
        given(tagRepository.findByName("Java")).willReturn(Optional.of(javaTag));
        
        // memberRepository 동작 정의
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));

        // boardRepository.save() 호출 시 반환할 Board 준비
        Board savedBoard = Board.builder()
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .category(requestDto.getCategory())
                .member(member)
                .build();
        
        given(boardRepository.save(any(Board.class))).willReturn(savedBoard);

        // when
        BoardResponseDto response = boardService.createBoard(1L, requestDto);

        // then
        assertThat(response.getTitle()).isEqualTo(requestDto.getTitle());
        assertThat(response.getContent()).isEqualTo(requestDto.getContent());
        assertThat(response.getCategory()).isEqualTo(requestDto.getCategory());
        assertThat(response.getAuthorName()).isEqualTo(member.getNickname());
        
        // 태그 저장 검증
        verify(tagRepository).findByName("Spring");
        verify(tagRepository).findByName("Java");
        verify(boardRepository).save(any(Board.class));
        verify(memberRepository).findById(1L);
    }

    @Test
    @DisplayName("게시글 조회 성공")
    void getBoardSuccess() {
        // given
        Long boardId = 1L;
        Member member = Member.builder()
                .email("test@example.com")
                .nickname("테스트유저")
                .role(Role.USER)
                .build();
            
        Board board = Board.builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .category(Category.FREE)
                .member(member)
                .build();

        given(boardRepository.findById(boardId)).willReturn(Optional.of(board));

        // when
        BoardResponseDto response = boardService.getBoard(boardId);

        // then
        assertThat(response.getTitle()).isEqualTo(board.getTitle());
        assertThat(response.getContent()).isEqualTo(board.getContent());
    }

    @Test
    @DisplayName("게시글 목록 조회 성공")
    void getBoardListSuccess() {
        // given
        PageRequest pageRequest = PageRequest.of(0, 10);
        Member testMember = Member.builder()
                .email("test@example.com")
                .nickname("테스트유저")
                .role(Role.USER)
                .build();
            
        List<Board> boards = Arrays.asList(
            Board.builder()
                .title("테스트 제목1")
                .content("테스트 내용1")
                .category(Category.FREE)
                .member(testMember)
                .build(),
            Board.builder()
                .title("테스트 제목2")
                .content("테스트 내용2")
                .category(Category.FREE)
                .member(testMember)
                .build()
        );
        Page<Board> boardPage = new PageImpl<>(boards);

        given(boardRepository.findByCategory(Category.FREE, pageRequest)).willReturn(boardPage);

        // when
        Page<BoardResponseDto> response = boardService.getBoardList(Category.FREE, pageRequest);

        // then
        assertThat(response.getContent()).hasSize(2);
        assertThat(response.getContent().get(0).getTitle()).isEqualTo("테스트 제목1");
    }

    @Test
    @DisplayName("게시글 수정 성공")
    void updateBoardSuccess() {
        // given
        Long boardId = 1L;
        Long memberId = 1L;
        Member member = createTestMember(memberId);
        Board board = createTestBoard(boardId, member);

        BoardRequestDto requestDto = BoardRequestDto.builder()
                .title("수정된 제목")
                .content("수정된 내용")
                .category(Category.FREE)
                .tags(Arrays.asList("Spring", "Java"))
                .build();

        Tag springTag = new Tag("Spring");
        Tag javaTag = new Tag("Java");
        given(tagRepository.findByName("Spring")).willReturn(Optional.of(springTag));
        given(tagRepository.findByName("Java")).willReturn(Optional.of(javaTag));
        given(boardRepository.findById(boardId)).willReturn(Optional.of(board));

        // when
        BoardResponseDto response = boardService.updateBoard(boardId, memberId, requestDto);

        // then
        assertThat(response.getTitle()).isEqualTo(requestDto.getTitle());
        assertThat(response.getContent()).isEqualTo(requestDto.getContent());
        assertThat(response.getCategory()).isEqualTo(requestDto.getCategory());
        verify(tagRepository).findByName("Spring");
        verify(tagRepository).findByName("Java");
    }

    @Test
    @DisplayName("게시글 삭제 성공")
    void deleteBoardSuccess() {
        // given
        Long boardId = 1L;
        Long memberId = 1L;
        Member member = createTestMember(memberId);
        Board board = createTestBoard(boardId, member);

        given(boardRepository.findById(boardId)).willReturn(Optional.of(board));

        // when
        boardService.deleteBoard(boardId, memberId);

        // then
        verify(boardRepository).delete(board);
    }

    @Test
    @DisplayName("존재하지 않는 게시글 조회 시 예외 발생")
    void getBoardNotFound() {
        // given
        Long boardId = 999L;
        given(boardRepository.findById(boardId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> boardService.getBoard(boardId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Board not found");
    }
} 
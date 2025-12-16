package ding.co.backendportfolio.chapter2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ding.co.backendportfolio.chapter2.dto.BoardRequestDto;
import ding.co.backendportfolio.chapter2.dto.BoardResponseDto;
import ding.co.backendportfolio.chapter2.dto.MemberResponse;
import ding.co.backendportfolio.chapter2.entity.Board;
import ding.co.backendportfolio.chapter2.entity.Category;
import ding.co.backendportfolio.chapter2.entity.Member;
import ding.co.backendportfolio.chapter2.entity.Role;
import ding.co.backendportfolio.chapter2.service.BoardAnalyticsService;
import ding.co.backendportfolio.chapter2.service.BoardService;
import ding.co.backendportfolio.chapter2.service.BoxingService;
import ding.co.backendportfolio.chapter2.service.MemberService;
import ding.co.backendportfolio.config.TestConfig;
import ding.co.backendportfolio.config.TestDatabaseConfig;
import ding.co.backendportfolio.config.TestMeterRegistryConfig;
import ding.co.backendportfolio.config.TestSecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BoardController.class)
@Import({TestConfig.class, TestSecurityConfig.class, TestMeterRegistryConfig.class, TestDatabaseConfig.class})
@AutoConfigureDataJpa
class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BoardService boardService;

    @MockBean
    private BoxingService boxingService;

    @MockBean
    private MemberService memberService;

    @MockBean
    private BoardAnalyticsService boardAnalyticsService;

    @Test
    @WithMockUser
    @DisplayName("게시글 생성 API")
    void createBoard() throws Exception {
        // given
        BoardRequestDto requestDto = BoardRequestDto.builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .category(Category.FREE)
                .tags(Arrays.asList("Spring", "Java"))
                .build();

        Member testMember = Member.builder()
                .email("test@example.com")
                .nickname("테스트유저")
                .role(Role.USER)
                .build();

        Board testBoard = Board.builder()
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .category(requestDto.getCategory())
                .member(testMember)
                .build();

        BoardResponseDto responseDto = new BoardResponseDto(testBoard);

        given(memberService.getCurrentMember(any())).willReturn(new MemberResponse(testMember));
        given(boardService.createBoard(any(), any())).willReturn(responseDto);

        // when & then
        mockMvc.perform(post("/api/chapter2/boards")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(requestDto.getTitle()))
                .andExpect(jsonPath("$.content").value(requestDto.getContent()))
                .andExpect(jsonPath("$.category").value(requestDto.getCategory().toString()));
    }

    @Test
    @DisplayName("게시글 조회 API")
    void getBoard() throws Exception {
        // given
        Member testMember = Member.builder()
                .email("test@example.com")
                .role(Role.USER)
                .nickname("테스트유저")
                .build();

        Board testBoard = Board.builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .category(Category.FREE)
                .member(testMember)
                .build();

        BoardResponseDto responseDto = new BoardResponseDto(testBoard);

        given(boardService.getBoard(any())).willReturn(responseDto);

        // when & then
        mockMvc.perform(get("/api/chapter2/boards/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(testBoard.getTitle()))
                .andExpect(jsonPath("$.content").value(testBoard.getContent()))
                .andExpect(jsonPath("$.category").value(testBoard.getCategory().toString()));
    }

    @Test
    @DisplayName("게시글 목록 조회 API")
    void getBoardList() throws Exception {
        // given
        Page<BoardResponseDto> responsePage = new PageImpl<>(Collections.emptyList());
        given(boardService.getBoardList(any(), any())).willReturn(responsePage);

        // when & then
        mockMvc.perform(get("/api/chapter2/boards")
                        .param("category", "FREE")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    @DisplayName("게시글 수정 API")
    void updateBoard() throws Exception {
        // given
        BoardRequestDto requestDto = BoardRequestDto.builder()
                .title("수정된 제목")
                .content("수정된 내용")
                .category(Category.FREE)
                .build();

        Member testMember = Member.builder()
                .email("test@example.com")
                .nickname("테스트유저")
                .role(Role.USER)
                .build();

        Board testBoard = Board.builder()
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .category(requestDto.getCategory())
                .member(testMember)
                .build();

        BoardResponseDto responseDto = new BoardResponseDto(testBoard);

        given(memberService.getCurrentMember(any())).willReturn(new MemberResponse(testMember));
        given(boardService.updateBoard(any(), any(), any())).willReturn(responseDto);

        // when & then
        mockMvc.perform(put("/api/chapter2/boards/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(requestDto.getTitle()))
                .andExpect(jsonPath("$.content").value(requestDto.getContent()))
                .andExpect(jsonPath("$.category").value(requestDto.getCategory().toString()));
    }

    @Test
    @WithMockUser
    @DisplayName("게시글 삭제 API")
    void deleteBoard() throws Exception {
        // given
        Member testMember = Member.builder()
                .email("test@example.com")
                .nickname("테스트유저")
                .role(Role.USER)
                .build();
        given(memberService.getCurrentMember(any())).willReturn(new MemberResponse(testMember));

        // when & then
        mockMvc.perform(delete("/api/chapter2/boards/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
} 
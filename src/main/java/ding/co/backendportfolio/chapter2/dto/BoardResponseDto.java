package ding.co.backendportfolio.chapter2.dto;

import ding.co.backendportfolio.chapter2.entity.Board;
import ding.co.backendportfolio.chapter2.entity.Category;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
public class BoardResponseDto {
    private Long id;
    private String title;
    private String content;
    private Category category;
    private int viewCount;
    private int likeCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String authorName;
    private List<String> tags;
    private Map<String, Long> tagStats;
    private Set<String> relatedTags;

    public BoardResponseDto(Board board) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.category = board.getCategory();
        this.viewCount = board.getViewCount();
        this.likeCount = board.getLikes().size();
        this.createdAt = board.getCreatedAt();
        this.updatedAt = board.getUpdatedAt();
        this.authorName = board.getMember().getNickname();
        this.tags = board.getBoardTags().stream()
                .map(boardTag -> boardTag.getTag().getName())
                .collect(Collectors.toList());
    }
}
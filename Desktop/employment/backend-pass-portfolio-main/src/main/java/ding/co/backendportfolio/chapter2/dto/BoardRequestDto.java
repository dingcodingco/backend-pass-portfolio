package ding.co.backendportfolio.chapter2.dto;

import ding.co.backendportfolio.chapter2.entity.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class BoardRequestDto {
    @NotBlank(message = "제목은 필수입니다")
    private String title;
    
    @NotBlank(message = "내용은 필수입니다")
    private String content;
    
    @NotNull(message = "카테고리는 필수입니다")
    private Category category;
    
    private List<String> tags = new ArrayList<>();

    @Builder
    public BoardRequestDto(String title, String content, Category category, List<String> tags) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.tags = tags != null ? tags : new ArrayList<>();
    }
}
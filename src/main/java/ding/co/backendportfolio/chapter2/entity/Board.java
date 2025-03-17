package ding.co.backendportfolio.chapter2.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ding.co.backendportfolio.global.BaseTimeEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.HashSet;

@Entity
@Getter
@Table(name = "ch2_boards")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Column(nullable = false)
    private int viewCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    private List<BoardLike> likes = new ArrayList<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BoardTag> boardTags = new HashSet<>();

    @Builder
    public Board(String title, String content, Category category, Member member) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.member = member;
    }

    public void update(String title, String content, Category category) {
        this.title = title;
        this.content = content;
        this.category = category;
    }

    public void increaseViewCount() {
        this.viewCount++;
    }

    public void updateTags(Set<Tag> newTags) {
        // 삭제할 태그 처리
        boardTags.removeIf(boardTag -> !newTags.contains(boardTag.getTag()));
        
        // 새로 추가할 태그 처리
        Set<Tag> currentTags = boardTags.stream()
                .map(BoardTag::getTag)
                .collect(Collectors.toSet());
        
        newTags.stream()
                .filter(tag -> !currentTags.contains(tag))
                .forEach(tag -> boardTags.add(new BoardTag(this, tag)));
    }

    public void setBoardTags(Set<BoardTag> boardTags) {
        this.boardTags = boardTags;
        boardTags.forEach(boardTag -> boardTag.setBoard(this));
    }
}
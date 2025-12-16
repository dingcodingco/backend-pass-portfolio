package ding.co.backendportfolio.chapter2.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "ch2_board_tags")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    public BoardTag(Board board, Tag tag) {
        this.board = board;
        this.tag = tag;
    }

    public void setBoard(Board board) {
        this.board = board;
    }
}
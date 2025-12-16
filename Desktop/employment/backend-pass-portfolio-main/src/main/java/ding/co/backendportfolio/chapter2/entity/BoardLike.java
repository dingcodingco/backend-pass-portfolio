package ding.co.backendportfolio.chapter2.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ding.co.backendportfolio.global.BaseTimeEntity;

@Entity
@Getter
@Table(name = "ch2_board_likes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardLike extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    public BoardLike(Member member, Board board) {
        this.member = member;
        this.board = board;
    }
}
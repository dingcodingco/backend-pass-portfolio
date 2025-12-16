package ding.co.backendportfolio.chapter6._1_practice;


import jakarta.persistence.*;
import lombok.*;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ch6_book")
@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private boolean isSoldOut;

    @Builder
    public Book(String name, boolean isSoldOut) {
        this.name = name;
        this.isSoldOut = isSoldOut;
    }
}

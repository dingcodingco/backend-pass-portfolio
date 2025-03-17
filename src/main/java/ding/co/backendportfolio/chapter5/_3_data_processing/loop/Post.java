package ding.co.backendportfolio.chapter5._3_data_processing.loop;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Post {
    private Long id;
    private PostType postType;
    private String content;

    public Post(Long id, PostType postType, String content) {
        this.id = id;
        this.postType = postType;
        this.content = content;
    }
}

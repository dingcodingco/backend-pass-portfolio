package ding.co.backendportfolio.chapter5._3_data_processing;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Post {
    private Long id;
    private PostType postType;
    private String content;

    @Builder
    public Post(Long id, String content) {
        this.id = id;
        this.postType = PostType.getByRandom();
        this.content = content;
    }

    /**
     * - 비용: 1
     * - 선택도: 0.33
     * => FilterOverhead: 1 * 0.33 = 0.33
     */
    public boolean isVideoType() {
        FilterUtil.simulateCost(1);
        return postType == PostType.VIDEO;
    }
}

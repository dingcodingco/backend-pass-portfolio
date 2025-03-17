package ding.co.backendportfolio.chapter5._3_data_processing.loop;

import java.util.List;
import java.util.stream.IntStream;

public class LoopTestDataUtil {
    public static List<Post> createTestData() {
        return IntStream.range(1, 10_000)
                .mapToObj(intValue -> Post.builder()
                        .id((long) intValue)
                        .postType(PostType.getByInt(intValue))
                        .build()
                )
                .toList();
    }
}

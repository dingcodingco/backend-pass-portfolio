package ding.co.backendportfolio.chapter5._3_data_processing.loop;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
class LoopService {

    private final ExternalApiClient externalApiClient;

    /**
     * 일반적인 예시: O(N^2)
     *
     * 정해진 시간 동안 작성된 게시글들에 대해 감정 분석을 수행하고,
     * 게시글 타입별 긍정 게시글 수를 계산합니다.
     *
     * @param posts 정해진 시간 동안 작성된 게시글 목록.
     * @return 각 게시글 타입별 긍정 게시글 수를 반환
     */
    public Map<PostType, Long> execute(List<Post> posts) {
        // 1. 외부 API를 통해 모든 게시글에 Sentiment 분석 수행
        List<SentimentAnalysisResult> sentimentAnalysisResults = externalApiClient.analyzeSentiments(posts);

        // 2. postive 만 필터링
        List<SentimentAnalysisResult> positiveAnalysisResults = sentimentAnalysisResults.stream()
                .filter(result -> result.getSentiment() == Sentiment.POSITIVE)
                .collect(Collectors.toList());

        // 3. 게시글 타입별 긍정 게시글 수
        return posts.stream()
                .filter(post ->
                        positiveAnalysisResults.stream()
                                .anyMatch(positiveAnalysisResult ->
                                        positiveAnalysisResult.getPostId().equals(post.getId())
                                )
                )
                .collect(Collectors.groupingBy(post -> post.getPostType(), Collectors.counting()));
    }
}
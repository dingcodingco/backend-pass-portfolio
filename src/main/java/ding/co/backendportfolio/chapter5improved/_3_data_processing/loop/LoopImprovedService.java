package ding.co.backendportfolio.chapter5improved._3_data_processing.loop;

import ding.co.backendportfolio.chapter5._3_data_processing.loop.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
class LoopImprovedService {

    private final ExternalApiClient externalApiClient;

    /**
     * 최적화 예시: O(N)
     * <p>
     * 정해진 시간 동안 작성된 게시글들에 대해 감정 분석을 수행하고,
     * 게시글 타입별 긍정 게시글 수를 계산합니다.
     *
     * @param posts 정해진 시간 동안 작성된 게시글 목록.
     * @return 각 게시글 타입별 긍정 게시글 수를 반환
     */
    public Map<PostType, Long> excuteImproved(List<Post> posts) {
        // 1. 외부 API를 통해 모든 게시글에 대해 POSITIVE 감정 분석 수행
        List<SentimentAnalysisResult> sentimentAnalysisResults = externalApiClient.analyzeSentiments(posts);

        // 2. postive 만 필터링 + List 를 Set 으로 변환
        Set<Long> positivePostIds = sentimentAnalysisResults.stream()
                .filter(result -> result.getSentiment() == Sentiment.POSITIVE)
                .map(SentimentAnalysisResult::getPostId)
                .collect(Collectors.toSet());

        // 3. 게시글 타입별 긍정 게시글 수
        return posts.stream()
                .filter(post -> positivePostIds.contains(post.getId()))
                .collect(Collectors.groupingBy(post -> post.getPostType(), Collectors.counting()));
    }
}